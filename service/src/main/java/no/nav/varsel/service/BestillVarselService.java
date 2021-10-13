package no.nav.varsel.service;

import com.google.common.collect.Maps;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.functional.VarselTekstMissingException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingAlreadyExistException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingNotExistException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class BestillVarselService {

	private static final Logger LOGG = LoggerFactory.getLogger(BestillVarselService.class);

	@Inject
	private AktoerService aktoerService;

	@Inject
	private VarselbestillingRepo varselbestillingRepo;

	@Inject
	private VarselInfoConsumer varselInfoConsumer;

	@Inject
	private HentDigitalKontaktinformasjonConsumer dkifConsumer;

	@Inject
	private VarselBestillingDomainMapper domainMapper;

	@Inject
	private VarselutsendingProducer varselutsendingProducer;

	@Inject
	private VarselutsendingToMapper varselutsendingToMapper;

	public void bestillVarsel(BestillVarselTo to) {
		Varselbestilling existingVarsel = varselbestillingRepo.findByVarselbestillingIdEager(to.getVarselBestillingId());

		if (to.getUtloepstidspunkt() != null && to.getUtloepstidspunkt().isBefore(LocalDateTime.now())) {
			throw new VarselbestillingUtloeptException(to.getVarselBestillingId(), to.getUtloepstidspunkt());
		}

		if (to.isRevarsling()) {
			assertRevarsel(to, existingVarsel);
		} else if (existingVarsel != null) {
			throw new VarselbestillingAlreadyExistException(to.getVarselBestillingId());
		}

		if (to.isRevarsling()) {
			bestillRevarsel(to, existingVarsel);
		} else {
			bestillFoerstegangsVarsel(to);
		}
	}

	private void assertRevarsel(BestillVarselTo to, Varselbestilling existingVarsel) {
		if (existingVarsel == null) {
			throw new VarselbestillingNotExistException(to.getVarselBestillingId());
		} else {
			Integer antallRevarslinger = existingVarsel.getAntallRevarslinger();
			LocalDate nesteVarslingDato = existingVarsel.getNesteVarslingDato();

			// Check to prevent errors from accidentally sending duplicate varsels, or secondary revarsel too early.
			// Could happen if BVARSEL001 is run twice in a short amount of time, before TVARSEL003 processes all the messages
			if (antallRevarslinger == null || antallRevarslinger <= 0 ||
					nesteVarslingDato == null || nesteVarslingDato.isAfter(LocalDate.now())) {
				throw new VarselbestillingAlreadyExistException(
						to.getVarselBestillingId(), antallRevarslinger, nesteVarslingDato);
			}
		}
	}

	private void bestillFoerstegangsVarsel(BestillVarselTo to) {
		to.setMottaker(aktoerService.findMissingAktoer(to));

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(to.getVarseltypeId());
		KontaktregisterTo kontaktregisterTo = dkifConsumer
				.hentDigitalKontaktinformasjonAndDecideKanal(to.getPersonIdent(), varselInfoTo.getPreferertKanal());

		Varselbestilling varselbestilling = domainMapper
				.mapVarselbestillingFoerstegangVarselMedRevarsel(to, varselInfoTo, kontaktregisterTo);

		varselbestillingRepo.saveAndFlush(varselbestilling);

		sendToVarselutsending(varselbestilling, to.getUtloepstidspunkt(), varselbestilling.getVarsels());
	}

	private void bestillRevarsel(BestillVarselTo to, Varselbestilling existingVarsel) {
		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(to.getVarseltypeId());

		KontaktregisterTo kontaktregisterTo = dkifConsumer
				.hentDigitalKontaktinformasjonAndDecideKanal(existingVarsel.getFnr(), varselInfoTo.getPreferertKanal());

		to.setParameters(Maps.newHashMap(existingVarsel.getFletteParametere()));
		try {
			Set<Varsel> varsels = kontaktregisterTo.getKanaler().stream()
					.map(kanalCode -> domainMapper.mapReVarsel(kanalCode, to, varselInfoTo, kontaktregisterTo))
					.peek(existingVarsel::addVarsel)
					.collect(toSet());

			updateRevarselFields(existingVarsel);

			varselbestillingRepo.saveAndFlush(existingVarsel);

			sendToVarselutsending(existingVarsel, to.getUtloepstidspunkt(), varsels);

		} catch (VarselTekstMissingException exception) {
			stopRevarsel(existingVarsel, exception);
		}
	}

	private void updateRevarselFields(Varselbestilling item) {
		Integer nyAntallRevarslinger = item.getAntallRevarslinger() - 1;
		if (nyAntallRevarslinger <= 0) {
			item.setAntallRevarslinger(null);
			item.setNesteVarslingDato(null);
			LOGG.info("Ingen nye revarsler for varselbestillingId={}", item.getVarselbestillingId());
		} else {
			item.setAntallRevarslinger(nyAntallRevarslinger);
			item.setNesteVarslingDato(LocalDate.now().plusDays(item.getRevarslingIntervall()));
			LOGG.info("BestillVarselService har bestilt {} nye varsler for varselbestillingId={}. Neste revarsel: {}",
					item.getAntallRevarslinger(), item.getVarselbestillingId(), item.getNesteVarslingDato());
		}
	}

	private void stopRevarsel(Varselbestilling existingVarsel, VarselTekstMissingException exception) {
		existingVarsel.setAntallRevarslinger(null);
		existingVarsel.setNesteVarslingDato(null);
		varselbestillingRepo.saveAndFlush(existingVarsel);
		throw exception;
	}

	private void sendToVarselutsending(Varselbestilling varselbestilling, LocalDateTime utloepstidspunkt, Set<Varsel> varsels) {
		List<VarselutsendingTo> varselutsendingTos = varselutsendingToMapper
				.mapVarsels(varselbestilling, utloepstidspunkt, varsels);

		for (VarselutsendingTo varselutsendingTo : varselutsendingTos) {
			varselutsendingProducer.produce(varselutsendingTo);
			LOGG.info("Sending distribusjonsvarsel with varselbestillingsId=" + varselbestilling.getVarselbestillingId()
					+ ", varselTypeId=" + varselbestilling.getVarseltypeId()
					+ " to kanal=" + varselutsendingTo.getKanal());
		}
	}
}
