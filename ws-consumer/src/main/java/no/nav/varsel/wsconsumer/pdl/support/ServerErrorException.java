package no.nav.varsel.wsconsumer.pdl.support;

public class ServerErrorException extends RuntimeException {
	public ServerErrorException(String message) {
		super(message);
	}

	public ServerErrorException(Throwable cause, String message) {
		super(message, cause);
	}
}
