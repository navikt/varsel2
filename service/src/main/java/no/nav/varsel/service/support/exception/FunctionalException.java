package no.nav.varsel.service.support.exception;

/**
 * Superclass for functional exceptions
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public abstract class FunctionalException extends RuntimeException {

	public FunctionalException(String message) {
		super(message);
	}

	public FunctionalException(String message, Throwable cause) {
		super(message, cause);
	}
}
