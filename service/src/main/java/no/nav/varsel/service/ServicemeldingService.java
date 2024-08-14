package no.nav.varsel.service;

import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.DokmetConsumer;
import no.nav.varsel.consumer.dokmet.to.Varselinfo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.support.Varselutsending;
import no.nav.varsel.service.support.VarselutsendingMapper;
import no.nav.varsel.service.support.exception.functional.ServicemeldingMappingException;
import no.nav.varsel.service.support.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.BrukernotifikasjonMapper;
import no.nav.varsel.service.tvarsel001.support.NotifikasjonMapper;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import no.nav.varsel.tvarsel001.NotifikasjonPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static org.springframework.util.StringUtils.hasText;

public class ServicemeldingService {

	private static final Logger log = LoggerFactory.getLogger(ServicemeldingService.class);
	private static final EnumSet<KanalCode> SMS_OG_EPOST = EnumSet.of(EPOST, SMS);

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
	private NotifikasjonPublisher notifikasjonPublisher;

	@Autowired
	private NotifikasjonMapper notifikasjonMapper;

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
		Varselinfo varselinfo = dokmetConsumer.hentVarselInfo(bestilling.getVarseltypeId());
		validateVarselInfoForBestilling(bestilling, varselinfo);

		overridePreferertKanalForTestmelding(bestilling, varselinfo);

		KontaktregisterTo kontaktregisterTo = hentKontaktregisterTo(bestilling);

		//4.Bestem varslingskanal
		Collection<KanalCode> kanalCodes = varselKanalDecider.decideKanaler(kontaktregisterTo, varselinfo.getPreferertKanal());
		kontaktregisterTo.setKanaler(kanalCodes);

		//5. Flett varsel
		Varselbestilling varselbestilling = domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselinfo, kontaktregisterTo);

		//6. Register varsel i DB
		varselbestillingRepo.saveAndFlush(varselbestilling);

		//7. Varselutsending
		List<Varselutsending> varselutsendingList = varselutsendingMapper.map(varselbestilling);

		try {
			sendNotifikasjon(varselinfo, varselbestilling, varselutsendingList);

		} catch (ServicemeldingMappingException e) {
			log.error("Feil ved mapping av data til servicemelding med BestillingId={}. Feilmelding={}", bestilling.getVarselBestillingId(), e.getMessage());
		} catch (Exception e) {
			log.error("Ukjent feil ved sending av servicemelding med BestillingId={}. Feilmelding={}", bestilling.getVarselBestillingId(), e.getMessage());
			throw e;
		}

		log.info("Sender Servicemelding med BestillingId={}, VarseltypeId={} til kanal(er)={}",
				bestilling.getVarselBestillingId(),
				bestilling.getVarseltypeId(),
				varselutsendingList.stream().map(it -> it.getKanal().name()).toList());
	}

	private void sendNotifikasjon(Varselinfo varselinfo, Varselbestilling varselbestilling, List<Varselutsending> varselutsendingList) {
		Doknotifikasjon doknotifikasjon = null;
		BeskjedInput beskjed = null;
		NokkelInput nokkel = null;

		if (harUtsendingTilEpostEllerSms(varselutsendingList)) {
			doknotifikasjon = notifikasjonMapper.mapNotifikasjon(
					varselutsendingList,
					varselbestilling
			);
		}

		var dittNavTo = varselutsendingList.stream()
				.filter(it -> DITT_NAV.equals(it.getKanal()))
				.findAny();

		if (dittNavTo.isPresent()) {
			if (hasText(varselinfo.getMal(DITT_NAV).getFoerstegangsTekst())) {
				beskjed = brukernotifikasjonMapper.mapBeskjed(dittNavTo.get());
				nokkel = brukernotifikasjonMapper.mapNokkel(varselbestilling);

			} else {
				log.info("Varsel med kanal DITT_NAV, bestillingsId={} og varseltypeId={} mangler foerstegangstekst. Sender ikke beskjed til DittNAV.",
						varselbestilling.getVarselbestillingId(), varselbestilling.getVarseltypeId());
			}
		}

		if (doknotifikasjon != null) {
			notifikasjonPublisher.sendNotifikasjon(doknotifikasjon);
		}
		if (beskjed != null && nokkel != null) {
			brukernotifikasjonBeskjedPublisher.sendNotifikasjon(beskjed, nokkel);
		}
	}

	private KontaktregisterTo hentKontaktregisterTo(BestillVarselTo bestilling) {
		//3.5.Hent digital kontaktinformasjon
		return dkifConsumer.hentDigitalKontaktinformasjon(bestilling.getPersonIdent());
	}

	private boolean harUtsendingTilEpostEllerSms(List<Varselutsending> varselutsendingList) {
		return varselutsendingList.stream().anyMatch(it -> SMS_OG_EPOST.contains(it.getKanal()));
	}

	private void validateVarselInfoForBestilling(BestillVarselTo to, Varselinfo varselinfo) {
		if (varselinfo.isInaktiv() && !to.isTestvarsel()) {
			throw new VarselInaktivVarselmalException(to.getPersonIdent(), to.getVarseltypeId(), to.getVarselBestillingId());
		}
	}

	private void overridePreferertKanalForTestmelding(BestillVarselTo to, Varselinfo varselinfo) {
		if (to.isTestvarsel()) {
			varselinfo.setPreferertKanal(new HashSet<>(Arrays.asList(KanalCode.values())));
		}
	}

	private String getVarselbestillingId(BestillVarselTo bestilling) {
		if (bestilling.getVarselBestillingId() == null) {
			return UUID.randomUUID().toString();
		}
		return bestilling.getVarselBestillingId();
	}
}
