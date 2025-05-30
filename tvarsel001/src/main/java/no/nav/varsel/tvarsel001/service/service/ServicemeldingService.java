package no.nav.varsel.tvarsel001.service.service;

import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.DokmetConsumer;
import no.nav.varsel.consumer.dokmet.Varselinfo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.tvarsel001.service.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.tvarsel001.service.service.support.Varselutsending;
import no.nav.varsel.tvarsel001.service.service.support.VarselutsendingMapper;
import no.nav.varsel.exception.functional.ServicemeldingMappingException;
import no.nav.varsel.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.tvarsel001.service.service.to.BestillVarselTo;
import no.nav.varsel.tvarsel001.service.service.support.BrukernotifikasjonMapper;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;

public class ServicemeldingService {

	private static final Logger log = LoggerFactory.getLogger(ServicemeldingService.class);

	@Autowired
	private AktoerService aktoerService;

	@Autowired
	private DokmetConsumer dokmetConsumer;

	@Autowired
	private HentDigitalKontaktinformasjonConsumer dkifConsumer;

	@Autowired
	private VarselKanalDecider varselKanalDecider;

	@Autowired
	private VarselutsendingMapper varselutsendingMapper;

	@Autowired
	private VarselBestillingDomainMapper domainMapper;

	@Autowired
	private VarselbestillingRepo varselbestillingRepo;

	@Autowired
	private BrukernotifikasjonBeskjedPublisher brukernotifikasjonBeskjedPublisher;

	@Autowired
	private BrukernotifikasjonMapper brukernotifikasjonMapper;

	public void bestillServicemelding(BestillVarselTo bestilling) {
		if (bestilling.getUtloepstidspunkt() != null && bestilling.getUtloepstidspunkt().isBefore(LocalDateTime.now())) {
			throw new VarselbestillingUtloeptException(bestilling.getVarselBestillingId(), bestilling.getUtloepstidspunkt());
		}

		//1.1 Duplikatkontroll
		if (bestilling.getVarselBestillingId() != null && varselbestillingRepo.existsByVarselbestillingId(bestilling.getVarselBestillingId())) {
			log.info("Varselbestilling med bestillingId={} finnes allerede. Sender ikke nytt varsel.", bestilling.getVarselBestillingId());
			return;
		}

		bestilling.setVarselBestillingId(getVarselbestillingId(bestilling));

		//2.Hent Aktørid for Ident
		bestilling.setMottaker(aktoerService.findMissingAktoer(bestilling));

		//3.Hent Varselinfo
		Varselinfo varselinfo = dokmetConsumer.hentVarselinfo(bestilling.getVarseltypeId());
		validateVarselinfoForBestilling(bestilling, varselinfo);

		if (bestilling.isTestvarsel()) {
			varselinfo = varselinfo.withPreferertKanal(Set.of(KanalCode.values()));
		}

		KontaktregisterTo kontaktregisterTo = dkifConsumer.hentDigitalKontaktinformasjon(bestilling.getPersonIdent());

		//4.Bestem varslingskanal
		Set<KanalCode> kanaler = varselKanalDecider.decideKanaler(kontaktregisterTo, varselinfo.getPreferertKanal());
		kontaktregisterTo.setKanaler(kanaler);

		//5. Flett varsel
		Varselbestilling varselbestilling = domainMapper.mapVarselbestilling(bestilling, varselinfo, kontaktregisterTo);

		//6. Register varsel i DB
		varselbestillingRepo.saveAndFlush(varselbestilling);

		//7. Varselutsending
		List<Varselutsending> varselutsendingList = varselutsendingMapper.map(varselbestilling);

		try {
			sendBrukernotifikasjon(varselbestilling, varselutsendingList);
		} catch (ServicemeldingMappingException e) {
			log.error("Feil ved mapping av data til servicemelding med BestillingId={}. Feilmelding={}", bestilling.getVarselBestillingId(), e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Ukjent feil ved sending av servicemelding med BestillingId={}. Feilmelding={}", bestilling.getVarselBestillingId(), e.getMessage());
			throw e;
		}
	}

	private void sendBrukernotifikasjon(Varselbestilling varselbestilling, List<Varselutsending> varselutsendingList) {

		if (varselutsendingList.stream().noneMatch(it -> DITT_NAV.equals(it.getKanal()))) {
			log.info("Varsel med bestillingsId={} og varseltypeId={} mangler kanal=DITT_NAV. Oppretter ikke beskjed gjennom brukernotifikasjon.",
					varselbestilling == null ? null : varselbestilling.getVarselbestillingId(),
					varselbestilling == null ? null : varselbestilling.getVarseltypeId());
			return;
		}

		var beskjed = brukernotifikasjonMapper.mapBeskjed(varselutsendingList);
		var nokkel = brukernotifikasjonMapper.mapNokkel(varselbestilling);

		log.info("Sender brukernotifikasjon med bestillingId={}, varseltypeId={} til kanal(er)={}",
				varselbestilling.getVarselbestillingId(),
				varselbestilling.getVarseltypeId(),
				varselutsendingList.stream().map(it -> it.getKanal().name()).toList());

		brukernotifikasjonBeskjedPublisher.sendNotifikasjon(beskjed, nokkel);
	}

	private void validateVarselinfoForBestilling(BestillVarselTo to, Varselinfo varselinfo) {
		if (varselinfo.isInaktiv() && !to.isTestvarsel()) {
			throw new VarselInaktivVarselmalException(to.getPersonIdent(), to.getVarseltypeId(), to.getVarselBestillingId());
		}
	}

	private String getVarselbestillingId(BestillVarselTo bestilling) {
		if (bestilling.getVarselBestillingId() == null) {
			return UUID.randomUUID().toString();
		}
		return bestilling.getVarselBestillingId();
	}
}
