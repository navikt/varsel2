package no.nav.varsel.service;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.support.Varselutsending;
import no.nav.varsel.service.support.VarselutsendingMapper;
import no.nav.varsel.service.support.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.BrukernotifikasjonMapper;
import no.nav.varsel.service.tvarsel001.support.NotifikasjonMapper;
import no.nav.varsel.service.tvarsel006.support.NotifikasjonMedKontaktinfoMapper;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import no.nav.varsel.tvarsel001.NotifikasjonPublisher;
import no.nav.varsel.tvarsel006.NotifikasjonMedKontaktinfoPublisher;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
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
	private VarselInfoConsumer varselInfoConsumer;

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
	private NotifikasjonMedKontaktinfoPublisher notifikasjonMedKontaktinfoPublisher;

	@Autowired
	private NotifikasjonMedKontaktinfoMapper notifikasjonMedKontaktinfoMapper;

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

		//2.Hent Aktørid for Ident
		bestilling.setMottaker(aktoerService.findMissingAktoer(bestilling));

		//3.Hent Varselinfo
		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(bestilling.getVarseltypeId());
		bestilling.setVarselBestillingId(UUID.randomUUID().toString());
		validateVarselInfoForBestilling(bestilling, varselInfoTo);

		overridePreferertKanalForTestmelding(bestilling, varselInfoTo);

		KontaktregisterTo kontaktregisterTo;
		if (hasKontaktInfo(bestilling)) {
			//TVARSEL006 Path
			varselInfoTo.getPreferertKanal().remove(DITT_NAV);
			kontaktregisterTo = new KontaktregisterTo();
			kontaktregisterTo.setMobiltelefonnummer(bestilling.getMobiltelefonnummer() != null ? bestilling.getMobiltelefonnummer().trim() : null);
			kontaktregisterTo.setEpostadresse(bestilling.getEpost() != null ? bestilling.getEpost().trim() : null);
		} else {
			//3.5.Hent digital kontaktinformasjon
			//TVARSEL001 Path
			kontaktregisterTo = dkifConsumer.hentDigitalKontaktinformasjon(bestilling.getPersonIdent());
		}

		//4.Bestem varslingskanal
		Collection<KanalCode> kanalCodes = varselKanalDecider.decideKanaler(kontaktregisterTo, varselInfoTo.getPreferertKanal());
		kontaktregisterTo.setKanaler(kanalCodes);

		//5. Flett varsel
		Varselbestilling varselbestilling = domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo);

		//6. Register varsel i DB
		varselbestillingRepo.saveAndFlush(varselbestilling);

		//7. Varselutsending
		List<Varselutsending> varselutsendingList = varselutsendingMapper.map(varselbestilling);

		if (hasKontaktInfo(bestilling)) { //TVARSEL006
			notifikasjonMedKontaktinfoPublisher.sendVarsel(notifikasjonMedKontaktinfoMapper.mapNotifikasjonMedKontaktinfo(
					varselutsendingList,
					varselbestilling,
					bestilling
			));
		} else { //TVARSEL001
			if (harUtsendingTilEpostEllerSms(varselutsendingList)) {
				notifikasjonPublisher.sendNotifikasjon(notifikasjonMapper.mapNotifikasjon(
						varselutsendingList,
						varselbestilling
				));
			}

			var dittNavTo = varselutsendingList.stream()
					.filter(it -> DITT_NAV.equals(it.getKanal()))
					.findAny();

			if (dittNavTo.isPresent()) {
				if (hasText(varselInfoTo.getMal(DITT_NAV).getFoerstegangsTekst())) {
					brukernotifikasjonBeskjedPublisher.sendNotifikasjon(
							brukernotifikasjonMapper.mapBeskjed(varselInfoTo, dittNavTo.get()),
							brukernotifikasjonMapper.mapNokkel(varselbestilling)
					);
				} else {
					log.info("Varsel med kanal DITT_NAV, bestillingsId={} og varseltypeId={} mangler foerstegangstekst. Sender ikke beskjed til DittNAV.",
							varselbestilling.getVarselbestillingId(), varselbestilling.getVarseltypeId());
				}
			}
		}

		log.info("Sender {} med BestillingId={}, VarseltypeId={} til kanal(er)={}",
				hasKontaktInfo(bestilling) ? "ServicemeldingMedKontaktInfo" : "Servicemelding",
				bestilling.getVarselBestillingId(),
				bestilling.getVarseltypeId(),
				varselutsendingList.stream().map(it -> it.getKanal().name()).toList());
	}

	private boolean harUtsendingTilEpostEllerSms(List<Varselutsending> varselutsendingList) {
		return varselutsendingList.stream().anyMatch(it -> SMS_OG_EPOST.contains(it.getKanal()));
	}

	private boolean hasKontaktInfo(BestillVarselTo bestilling) {
		return hasText(bestilling.getMobiltelefonnummer()) || hasText(bestilling.getEpost());
	}

	private void validateVarselInfoForBestilling(BestillVarselTo to, VarselInfoTo varselInfoTo) {
		if (varselInfoTo.isInaktiv() && !to.isTestvarsel()) {
			throw new VarselInaktivVarselmalException(to.getPersonIdent(), to.getVarseltypeId(), to.getVarselBestillingId());
		}
	}

	private void overridePreferertKanalForTestmelding(BestillVarselTo to, VarselInfoTo varselInfoTo) {
		if (to.isTestvarsel()) {
			varselInfoTo.setPreferertKanal(new HashSet<>(Arrays.asList(KanalCode.values())));
		}
	}
}
