package no.nav.varsel.service;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.tvarsel005.support.BrukervarselMapper;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

import javax.inject.Inject;
import java.util.List;

/**
 * Service For HentVarselForBruker Tvarsel005
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BrukervarselV1Service {

	@Inject
	private VarselbestillingRepo varselbestillingRepo;
	@Inject
	private BrukervarselMapper mapper;

	public HentVarselForBrukerResponseTo hentVarselForBruker(HentVarselForBrukerTo reqTo) {
		String bruker = reqTo.getAktoerId() == null ? reqTo.getFnr() : reqTo.getAktoerId();

		List<Varselbestilling> list = varselbestillingRepo
				.findFerdigbehandletVarselbestillinger(bruker, reqTo.getDatoFom(), reqTo.getDatoTom());

		return mapper.map(list);
	}
}
