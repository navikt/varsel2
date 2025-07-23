package no.nav.varsel.exception.technical;

public class VarselTechnicalException extends RuntimeException {

	public VarselTechnicalException(String message) {
		super(message);
	}

	public VarselTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
