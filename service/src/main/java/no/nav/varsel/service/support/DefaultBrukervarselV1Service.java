package no.nav.varsel.service.support;

import no.nav.varsel.repo.TVARSEL005Repo;
import no.nav.varsel.service.BrukervarselV1Service;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

import javax.inject.Inject;

/**
 * @author Lars Aune
 */
public class DefaultBrukervarselV1Service implements BrukervarselV1Service {
	@Inject
	private TVARSEL005Repo tvarsel005Repo;
	@Override
	public HentVarselForBrukerResponseTo hentVarselForBruker(HentVarselForBrukerTo hentVarselForBrukerTo) {
		return null;
	}
}
