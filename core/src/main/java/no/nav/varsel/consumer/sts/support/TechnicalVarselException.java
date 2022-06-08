package no.nav.varsel.consumer.sts.support;

import no.nav.varsel.domain.exception.NoJmsBackoutException;


public abstract class TechnicalVarselException extends NoJmsBackoutException {

	public TechnicalVarselException(String message) {
		super(message);
	}

	public TechnicalVarselException(String message, Throwable cause) {
		super(message, cause);
	}
}
