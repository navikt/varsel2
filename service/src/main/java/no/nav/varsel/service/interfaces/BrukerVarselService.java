package no.nav.varsel.service.interfaces;

import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

/**
 * @author Roar Bjurstrom, Visma Consulting.
 */
public interface BrukervarselService {

	HentVarselForBrukerResponseTo hentVarselForBruker(HentVarselForBrukerTo reqTo);
}
