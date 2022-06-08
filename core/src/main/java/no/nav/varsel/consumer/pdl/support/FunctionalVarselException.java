package no.nav.varsel.consumer.pdl.support;

import no.nav.varsel.domain.exception.NoJmsBackoutException;

/**
 * Superclass for functional exceptions
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public abstract class FunctionalVarselException extends NoJmsBackoutException {

	public FunctionalVarselException(String message) {
		super(message);
	}

	public FunctionalVarselException(String message, Throwable cause) {
		super(message, cause);
	}
}
