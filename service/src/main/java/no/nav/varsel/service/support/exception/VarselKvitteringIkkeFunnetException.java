package no.nav.varsel.service.support.exception;

/**
 * Created by T133804 on 29.05.2017.
 */
public class VarselKvitteringIkkeFunnetException extends RuntimeException {
	public VarselKvitteringIkkeFunnetException(String message) {
		super(message);
	}
	
	public VarselKvitteringIkkeFunnetException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public VarselKvitteringIkkeFunnetException(Throwable cause) {
		super(cause);
	}
}
