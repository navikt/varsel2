package no.nav.varsel.wsconsumer.pdl.support;

import no.nav.varsel.domain.exception.NoJmsBackoutException;


public class AktoerIkkeFunnetException extends NoJmsBackoutException {
	public AktoerIkkeFunnetException(String message) {
		super(message);
	}

	public AktoerIkkeFunnetException(String message, Throwable cause) {
		super(message, cause);
	}
}
