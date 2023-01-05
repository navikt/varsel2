package no.nav.varsel.service;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.interfaces.BrukervarselService;
import no.nav.varsel.service.tvarsel005.support.BrukervarselMapper;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BrukervarselV1Service implements BrukervarselService {

	@Autowired
	private VarselbestillingRepo varselbestillingRepo;
	@Autowired
	private BrukervarselMapper mapper;

	public HentVarselForBrukerResponseTo hentVarselForBruker(HentVarselForBrukerTo reqTo) {
		String bruker = reqTo.getAktoerId() == null ? reqTo.getFnr() : reqTo.getAktoerId();

		List<Varselbestilling> list = varselbestillingRepo
				.findFerdigbehandletVarselbestillinger(bruker, reqTo.getDatoFom(), reqTo.getDatoTom());

		return mapper.map(list);
	}
}
