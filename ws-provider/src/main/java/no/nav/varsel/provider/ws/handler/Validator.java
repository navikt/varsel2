package no.nav.varsel.provider.ws.handler;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;

/**
 * Validates payload. Expected to throw Exception with describing message.
 *
 * @author Lars Aune
 */
public interface Validator<T> {
	void validate(T object) throws HentVarselForBrukerUgyldigInput;
}
