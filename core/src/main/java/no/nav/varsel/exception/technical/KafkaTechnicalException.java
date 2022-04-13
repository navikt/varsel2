package no.nav.varsel.exception.technical;

public class KafkaTechnicalException extends RuntimeException {
	public KafkaTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
