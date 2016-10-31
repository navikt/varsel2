package no.nav.varsel.service;

import static org.springframework.util.StringUtils.hasText;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Service for Servicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ServicemeldingService {

	private static final Logger LOGG = LoggerFactory.getLogger(ServicemeldingService.class);

	@Inject
	private AktoerService aktoerService;
	@Inject
	private VarselInfoConsumer varselInfoConsumer;
	@Inject
	private HentDigitalKontaktinformasjonConsumer dkifConsumer;
	@Inject
	private VarselKanalDecider varselKanalDecider;
	@Inject
	private VarselutsendingProducer varselutsendingProducer;
	@Inject
	private VarselutsendingToMapper varselutsendingToMapper;

	@Inject
	private VarselBestillingDomainMapper domainMapper;

	@Inject
	private VarselbestillingRepo varselbestillingRepo;

	public void bestillServicemelding(BestillVarselTo bestilling) {
		if (bestilling.getUtloepstidspunkt() != null && bestilling.getUtloepstidspunkt().isBefore(LocalDateTime.now())) {
			throw new VarselbestillingUtloeptException(bestilling.getVarselBestillingId(), bestilling.getUtloepstidspunkt());
		}

		AktoerTo fetchedAktoerTo = aktoerService.findMissingAktoer(bestilling);
		bestilling.setMottaker(fetchedAktoerTo);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(bestilling.getVarseltypeId());
		bestilling.setVarselBestillingId(UUID.randomUUID().toString());
		validateVarselInfoForBestilling(bestilling, varselInfoTo);

		overridePreferertKanalForTestmelding(bestilling, varselInfoTo);

		KontaktregisterTo kontaktregisterTo;
		if (hasKontaktInfo(bestilling)) {
			//TVARSEL006 Path
			varselInfoTo.getPreferertKanal().remove(KanalCode.DITT_NAV);
			kontaktregisterTo = new KontaktregisterTo();
			kontaktregisterTo.setMobiltelefonnummer(bestilling.getMobiltelefonnummer());
			kontaktregisterTo.setEpostadresse(bestilling.getEpost());
		} else {
			//TVARSEL001 Path
			kontaktregisterTo = dkifConsumer.hentDigitalKontaktinformasjon(bestilling.getPersonIdent());
		}

		Collection<KanalCode> kanalCodes = varselKanalDecider.decideKanaler(kontaktregisterTo, varselInfoTo.getPreferertKanal());
		kontaktregisterTo.setKanaler(kanalCodes);

		Varselbestilling varselbestilling = domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo);

		varselbestillingRepo.saveAndFlush(varselbestilling);

		List<VarselutsendingTo> varselutsendingTos = varselutsendingToMapper.map(varselbestilling);
		for (VarselutsendingTo varselutsendingTo : varselutsendingTos) {
			varselutsendingProducer.produce(varselutsendingTo);
			LOGG.info("Sending servicevarsel with varselbestillingsId=" + varselbestilling.getVarselbestillingId()
					+ ", varselTypeId=" + varselbestilling.getVarseltypeId()
					+ " to kanal=" + varselutsendingTo.getKanal());
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
