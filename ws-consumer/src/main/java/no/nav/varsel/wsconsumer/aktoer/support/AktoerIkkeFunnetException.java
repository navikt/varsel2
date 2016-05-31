package no.nav.varsel.wsconsumer.aktoer.support;

import no.nav.varsel.domain.exception.NoJmsBackoutException;

/**
 * Exception for HentIdentForAktoerId og HentAktoerIdForIdent
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerIkkeFunnetException extends NoJmsBackoutException {
	public AktoerIkkeFunnetException(String message, Throwable cause) {
		super(message, cause);
	}
}
