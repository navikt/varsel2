package no.nav.varsel.consumer.pdl.support;



public class PersonIkkeFunnetException extends FunctionalVarselException {
	public PersonIkkeFunnetException(String message) {
		super(message);
	}

	public PersonIkkeFunnetException(Throwable cause, String message) {
		super(message, cause);
	}
}
