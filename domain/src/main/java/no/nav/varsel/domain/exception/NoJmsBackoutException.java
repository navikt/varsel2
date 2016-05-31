package no.nav.varsel.domain.exception;

/**
 * Exception noting that the mssage should not fail and therefore not be placed on backout queue
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class NoJmsBackoutException extends RuntimeException {

	public NoJmsBackoutException(String message) {
		super(message);
	}

	public NoJmsBackoutException(String message, Throwable cause) {
		super(message, cause);
	}
}
