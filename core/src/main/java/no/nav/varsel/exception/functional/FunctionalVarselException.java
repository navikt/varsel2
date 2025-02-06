package no.nav.varsel.exception.functional;

import no.nav.varsel.domain.exception.NoJmsBackoutException;

public abstract class FunctionalVarselException extends NoJmsBackoutException {

	public FunctionalVarselException(String message) {
		super(message);
	}

	public FunctionalVarselException(String message, Throwable cause) {
		super(message, cause);
	}
}
