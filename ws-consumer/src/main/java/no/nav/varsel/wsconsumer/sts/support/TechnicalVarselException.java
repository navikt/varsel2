package no.nav.varsel.wsconsumer.sts.support;

import no.nav.varsel.domain.exception.NoJmsBackoutException;

/**
 * Superclass for functional exceptions
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public abstract class TechnicalVarselException extends NoJmsBackoutException {

	public TechnicalVarselException(String message) {
		super(message);
	}

	public TechnicalVarselException(String message, Throwable cause) {
		super(message, cause);
	}
}
