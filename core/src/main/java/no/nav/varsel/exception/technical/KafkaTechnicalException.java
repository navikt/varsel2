package no.nav.varsel.exception.technical;

public class KafkaTechnicalException extends VarselTechnicalException {
	public KafkaTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
