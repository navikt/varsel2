package no.nav.varsel.service;

import static java.util.stream.Collectors.toSet;

import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.VarselbestillingAlreadyExistException;
import no.nav.varsel.service.support.exception.VarselbestillingNotExistException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * Service for TVARSEL003 BestillVarsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselService {

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
		Varselbestilling existingVarsel = varselbestillingRepo.findByVarselbestillingId(to.getVarselBestillingId());

		boolean revarsling = to.isRevarsling();
		if (revarsling && existingVarsel == null) {
			throw new VarselbestillingNotExistException(to.getVarselBestillingId());
		} else if (!revarsling && existingVarsel != null) {
			throw new VarselbestillingAlreadyExistException(to.getVarselBestillingId());
		}

		if (revarsling) {
			bestillRevarsel(to, existingVarsel);
		} else {
			bestillFoerstegangsVarsel(to);
		}
	}

	private void bestillFoerstegangsVarsel(BestillVarselTo to) {
		AktoerTo origAktoer = aktoerService.completeAktoerPersonIdent(to);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(to.getVarslingstype());
		KontaktregisterTo kontaktregisterTo = dkifConsumer
				.hentDigitalKontaktinformasjonAndDecideKanal(to.getPersonIdent(), varselInfoTo.getPreferertKanal());

		Varselbestilling varselbestilling = domainMapper
				.mapVarselbestillingFoerstegangVarselMedRevarsel(to, varselInfoTo, kontaktregisterTo);

		varselbestillingRepo.saveAndFlush(varselbestilling);

		sendToVarselutsending(to, origAktoer, to.getVarslingstype(), varselbestilling.getVarsels());
	}

	private void bestillRevarsel(BestillVarselTo to, Varselbestilling existingVarsel) {
		AktoerTo origAktoer = to.createAktoerTo();

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(to.getVarslingstype());
		KontaktregisterTo kontaktregisterTo = dkifConsumer
				.hentDigitalKontaktinformasjonAndDecideKanal(existingVarsel.getFnr(), varselInfoTo.getPreferertKanal());

		Set<Varsel> varsels = kontaktregisterTo.getKanaler().stream()
				.map((kanalCode) -> domainMapper.mapReVarsel(kanalCode, to, varselInfoTo, kontaktregisterTo))
				.peek(existingVarsel::addVarsel)
				.collect(toSet());

		varselbestillingRepo.saveAndFlush(existingVarsel);

		sendToVarselutsending(to, origAktoer, to.getVarslingstype(), varsels);
	}

	private void sendToVarselutsending(BestillVarselTo to, AktoerTo origAktoer, String varslingstype, Set<Varsel> varsels) {
		List<VarselutsendingTo> varselutsendingTos = varselutsendingToMapper
				.mapVarsels(origAktoer, to.getUtloepstidspunkt(), varslingstype, varsels);

		for (VarselutsendingTo varselutsendingTo : varselutsendingTos) {
			varselutsendingProducer.produce(varselutsendingTo);
		}
	}
}
