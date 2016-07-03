package no.nav.varsel.provider.map;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;

/**
 * @author Lars Aune
 */
public interface HentVarselForBrukerResponseMapper {
	HentVarselForBrukerResponse map(HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo);
}
