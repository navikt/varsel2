package no.nav.varsel.wsconsumer.aktoer.support;

/**
 * Exception for HentIdentForAktoerId og HentAktoerIdForIdent
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerIkkeFunnetException extends RuntimeException {
	public AktoerIkkeFunnetException(String message, Throwable cause) {
		super(message, cause);
	}
}
