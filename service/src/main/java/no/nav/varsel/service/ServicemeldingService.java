package no.nav.varsel.service;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.tvarsel006.VarselUtsendelse;
import no.nav.varsel.service.tvarsel006.support.VarselUtsendelseMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static org.springframework.util.StringUtils.hasText;

public class ServicemeldingService {
	
	private static final Logger log = LoggerFactory.getLogger(ServicemeldingService.class);
	
	@Autowired
	private AktoerService aktoerService;

	@Autowired
	private VarselInfoConsumer varselInfoConsumer;

	@Autowired
	private HentDigitalKontaktinformasjonConsumer dkifConsumer;

	@Autowired
	private VarselKanalDecider varselKanalDecider;

	@Autowired
	private VarselutsendingProducer varselutsendingProducer;

	@Autowired
	private VarselutsendingToMapper varselutsendingToMapper;
	
	@Autowired
	private VarselBestillingDomainMapper domainMapper;
	
	@Autowired
	private VarselbestillingRepo varselbestillingRepo;

	@Autowired
	private VarselUtsendelse varselUtsendelse;

	@Autowired
	private VarselUtsendelseMapper varselUtsendelseMapper;

	public void bestillServicemelding(BestillVarselTo bestilling) {
		if (bestilling.getUtloepstidspunkt() != null && bestilling.getUtloepstidspunkt().isBefore(LocalDateTime.now())) {
			throw new VarselbestillingUtloeptException(bestilling.getVarselBestillingId(), bestilling.getUtloepstidspunkt());
		}
		
		bestilling.setMottaker(aktoerService.findMissingAktoer(bestilling));
		
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
			//TVARSEL001 Path
			kontaktregisterTo = dkifConsumer.hentDigitalKontaktinformasjon(bestilling.getPersonIdent());
		}
		
		Collection<KanalCode> kanalCodes = varselKanalDecider.decideKanaler(kontaktregisterTo, varselInfoTo.getPreferertKanal());
		kontaktregisterTo.setKanaler(kanalCodes);
		
		Varselbestilling varselbestilling = domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo);
		
		varselbestillingRepo.saveAndFlush(varselbestilling);
		List<VarselutsendingTo> varselutsendingTos = varselutsendingToMapper.map(varselbestilling);

		//TODO Blir en rework av denne logikken ved implementasjon av tvarsel001 funksjonalitet
		for (VarselutsendingTo varselutsendingTo : varselutsendingTos) {
			if(hasKontaktInfo(bestilling)) {
				varselUtsendelse.sendVarsel(varselUtsendelseMapper.mapNotifikasjonMedKontaktInfo(
						bestilling,
						varselbestilling,
						varselutsendingTo,
						varselInfoTo
				));
			} else {
				varselutsendingProducer.produce(varselutsendingTo);
			}

			log.info(String.format("Sender %s med BestillingsId=%s, VarselTypeId=%s til kanal=%s",
					hasKontaktInfo(bestilling) ? "ServicemeldingMedKontaktInfo" : "Servicemelding",
					varselbestilling.getVarselbestillingId(),
					varselbestilling.getVarseltypeId(),
					varselutsendingTo.getKanal()));
		}
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
