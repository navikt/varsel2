package no.nav.varsel.consumer.pdl.support;

public class ServerErrorException extends RuntimeException {
	public ServerErrorException(String message) {
		super(message);
	}

	public ServerErrorException(Throwable cause, String message) {
		super(message, cause);
	}
}
