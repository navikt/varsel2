package no.nav.varsel.service.interfaces;

import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

public interface BrukervarselService {

	/**
	 * Finds relevant varsels in a given period, based on fnr or aktorid
	 *
	 * @param reqTo The input object
	 * @return the varsels
	 */
	HentVarselForBrukerResponseTo hentVarselForBruker(HentVarselForBrukerTo reqTo);
}
