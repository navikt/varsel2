package no.nav.varsel.domain.exception;

public class NoJmsBackoutException extends RuntimeException {

	public NoJmsBackoutException(String message) {
		super(message);
	}

	public NoJmsBackoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoJmsBackoutException(Throwable cause) {
		super(cause);
	}
}
