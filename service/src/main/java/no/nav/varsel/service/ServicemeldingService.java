package no.nav.varsel.service;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.tvarsel001.support.ServicemeldingDomainMapper;
import no.nav.varsel.service.tvarsel001.to.BestillServicemeldingTo;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.aktoer.to.AktoerTo;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.DigitalKontaktinfoTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import javax.inject.Inject;
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
//	@Inject
//	private VarselutsendingProducer varselutsendingProducer;

	@Inject
	private ServicemeldingDomainMapper domainMapper;
	@Inject
	private VarselbestillingRepo varselbestillingRepo;

	public void bestillServicemelding(BestillServicemeldingTo bestillServicemeldingTo) {
		completePersonIdent(bestillServicemeldingTo);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(bestillServicemeldingTo.getVarslingstype());
		DigitalKontaktinfoTo digitalKontaktinfoTo = digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(bestillServicemeldingTo.getPersonIdent());

		// Move to dki consumer?
		Collection<KanalCode> kanaler = domainMapper.decideKanaler(digitalKontaktinfoTo, varselInfoTo);
		digitalKontaktinfoTo.setKanaler(kanaler);

		// TODO flett varsel PK-31739 - tekst med params til tekst?

		Varselbestilling varselbestilling = domainMapper.mapToDomain(bestillServicemeldingTo, varselInfoTo, digitalKontaktinfoTo);

		varselbestilling = varselbestillingRepo.save(varselbestilling);
	}

	private void completePersonIdent(BestillServicemeldingTo bestillServicemeldingTo) {
		AktoerTo origAktoer = bestillServicemeldingTo.craeteAktoerTo();
		AktoerTo fetchedAktoer = aktoerConsumer.hentIdent(origAktoer);
		bestillServicemeldingTo.setMottaker(fetchedAktoer);
	}

}
