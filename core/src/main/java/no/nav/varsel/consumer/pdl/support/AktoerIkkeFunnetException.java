package no.nav.varsel.consumer.pdl.support;

import no.nav.varsel.domain.exception.NoJmsBackoutException;


public class AktoerIkkeFunnetException extends NoJmsBackoutException {
	public AktoerIkkeFunnetException(String message) {
		super(message);
	}

}
