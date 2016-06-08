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

		Boolean revarsling = to.isRevarsling();
		if (revarsling && existingVarsel == null) {
			throw new VarselbestillingNotExistException(to.getVarselBestillingId());
		} else if (!revarsling && existingVarsel != null) {
			throw new VarselbestillingAlreadyExistException(to.getVarselBestillingId());
		}

		AktoerTo origAktoer = to.craeteAktoerTo();

		if (revarsling) {
			to.setMottaker(AktoerTo.newPersonIdent(existingVarsel.getFnr()));
		} else {
			aktoerService.completeAktoerPersonIdent(to);
		}

		String varslingstype = to.getVarslingstype();
		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(varslingstype);
		KontaktregisterTo kontaktregisterTo = dkifConsumer
				.hentDigitalKontaktinformasjonAndDecideKanal(to.getPersonIdent(), varselInfoTo.getPreferertKanal());

		Set<Varsel> varsels;
		Varselbestilling varselbestilling;

		if (revarsling) {
			varselbestilling = existingVarsel;
			varsels = kontaktregisterTo.getKanaler().stream()
					.map((kanalCode) -> domainMapper.mapReVarsel(kanalCode, to, varselInfoTo, kontaktregisterTo))
					.peek(existingVarsel::addVarsel)
					.collect(toSet());
		} else {
			varselbestilling = domainMapper.mapVarselbestillingFoerstegangVarselMedRevarsel(to, varselInfoTo, kontaktregisterTo);
			varsels = varselbestilling.getVarsels();
		}

		varselbestillingRepo.saveAndFlush(varselbestilling);

		List<VarselutsendingTo> varselutsendingTos = varselutsendingToMapper
				.mapVarsels(origAktoer, to.getUtloepstidspunkt(), varslingstype, varsels);

		for (VarselutsendingTo varselutsendingTo : varselutsendingTos) {
			varselutsendingProducer.produce(varselutsendingTo);
		}
	}
}
