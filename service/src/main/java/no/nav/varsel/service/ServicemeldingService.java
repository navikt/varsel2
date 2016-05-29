package no.nav.varsel.service;

import static javax.transaction.Transactional.TxType.MANDATORY;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingMapper;
import no.nav.varsel.service.tvarsel001.support.ServicemeldingDomainMapper;
import no.nav.varsel.service.tvarsel001.to.BestillServicemeldingTo;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.aktoer.to.AktoerTo;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Service for Servicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ServicemeldingService {

	@Inject
	private AktoerConsumer aktoerConsumer;
	@Inject
	private VarselInfoConsumer varselInfoConsumer;
	@Inject
	private HentDigitalKontaktinformasjonConsumer digitalKontaktinformasjonConsumer;

	@Inject
	private VarselutsendingProducer varselutsendingProducer;
	@Inject
	private VarselutsendingMapper varselutsendingMapper;

	@Inject
	private ServicemeldingDomainMapper domainMapper;
	@Inject
	private VarslelKanalDecider varslelKanalDecider;
	@Inject
	private VarselbestillingRepo varselbestillingRepo;

	@Transactional(MANDATORY)
	public void bestillServicemelding(BestillServicemeldingTo bestillServicemeldingTo) {
		completePersonIdent(bestillServicemeldingTo);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(bestillServicemeldingTo.getVarslingstype());
		KontaktregisterTo kontaktregisterTo = digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(bestillServicemeldingTo.getPersonIdent());

		// Move to dki consumer?
		Collection<KanalCode> kanaler = varslelKanalDecider.decideKanaler(kontaktregisterTo, varselInfoTo.getPreferertKanal());
		kontaktregisterTo.setKanaler(kanaler);

		Varselbestilling varselbestilling = domainMapper.mapToDomain(bestillServicemeldingTo, varselInfoTo, kontaktregisterTo);

		varselbestilling = varselbestillingRepo.save(varselbestilling);

		try {
			VarselutsendingTo varselutsendingTo = varselutsendingMapper.map(varselbestilling);
			varselutsendingProducer.produce(varselutsendingTo);

			varselbestilling.getVarsels().forEach(varsel -> {
				varsel.setSendtTidspunkt(LocalDateTime.now());
				varsel.setStatus(StatusCode.SENDT);
			});

			varselbestillingRepo.save(varselbestilling);
		} catch (Exception e) {
			varselbestillingRepo.delete(varselbestilling.getId());
//			throw new PlaceOnBackoutException("Varselutsending failed", e);
			throw e;
		}
	}

	private void completePersonIdent(BestillServicemeldingTo bestillServicemeldingTo) {
		AktoerTo origAktoer = bestillServicemeldingTo.craeteAktoerTo();
		AktoerTo fetchedAktoer = aktoerConsumer.hentIdent(origAktoer);
		bestillServicemeldingTo.setMottaker(fetchedAktoer);
	}

}
