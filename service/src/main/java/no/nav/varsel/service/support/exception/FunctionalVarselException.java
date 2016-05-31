package no.nav.varsel.service.support.exception;

/**
 * Superclass for functional exceptions
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public abstract class FunctionalVarselException extends RuntimeException {

	public FunctionalVarselException(String message) {
		super(message);
	}

	public FunctionalVarselException(String message, Throwable cause) {
		super(message, cause);
	}
}
