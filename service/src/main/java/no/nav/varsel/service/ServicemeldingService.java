package no.nav.varsel.service;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.VarselInaktivVarselmalException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Service for Servicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ServicemeldingService {

	@Inject
	private AktoerService aktoerService;
	@Inject
	private VarselInfoConsumer varselInfoConsumer;
	@Inject
	private HentDigitalKontaktinformasjonConsumer dkifConsumer;

	@Inject
	private VarselutsendingProducer varselutsendingProducer;
	@Inject
	private VarselutsendingToMapper varselutsendingToMapper;

	@Inject
	private VarselBestillingDomainMapper domainMapper;

	@Inject
	private VarselbestillingRepo varselbestillingRepo;

	public void bestillServicemelding(BestillVarselTo bestilling) {
		AktoerTo origAktoer = aktoerService.completeAktoerPersonIdent(bestilling);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(bestilling.getVarseltypeId());
		validateVarselInfoForBestilling(bestilling, varselInfoTo);
		applyPreferertKanalForTestmelding(bestilling, varselInfoTo);

		KontaktregisterTo kontaktregisterTo = dkifConsumer
				.hentDigitalKontaktinformasjonAndDecideKanal(bestilling.getPersonIdent(), varselInfoTo.getPreferertKanal());

		bestilling.setVarselBestillingId(UUID.randomUUID().toString());
		Varselbestilling varselbestilling = domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo);

		varselbestillingRepo.saveAndFlush(varselbestilling);

		List<VarselutsendingTo> varselutsendingTos = varselutsendingToMapper.map(varselbestilling, origAktoer);
		for (VarselutsendingTo varselutsendingTo : varselutsendingTos) {
			varselutsendingProducer.produce(varselutsendingTo);
		}
	}

	private void validateVarselInfoForBestilling(BestillVarselTo to, VarselInfoTo varselInfoTo) {
		if (varselInfoTo.isInaktiv() && !to.isTestvarsel()) {
			throw new VarselInaktivVarselmalException(to.getPersonIdent(), to.getVarseltypeId());
		}
	}

	private void applyPreferertKanalForTestmelding(BestillVarselTo to, VarselInfoTo varselInfoTo) {
		if (to.isTestvarsel()) {
			varselInfoTo.setPreferertKanal(new HashSet<>(Arrays.asList(KanalCode.values())));
		}
	}
}
