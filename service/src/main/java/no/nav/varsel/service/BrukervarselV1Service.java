package no.nav.varsel.service;

import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

/**
 * @author Lars Aune
 */
public interface BrukervarselV1Service {
	HentVarselForBrukerResponseTo hentVarselForBruker(HentVarselForBrukerTo hentVarselForBrukerTo);
}
