package no.nav.varsel.provider.map;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

/**
 * @author Lars Aune
 */
public interface HentVarselForBrukerRequestMapper {
	HentVarselForBrukerTo map(HentVarselForBrukerRequest request);
}
