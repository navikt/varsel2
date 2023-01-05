package no.nav.varsel.service.support.exception.functional;

public class FletteparameterMissingException extends FunctionalVarselException {
	public FletteparameterMissingException(String message) {
		super("Not all parameters given for varsel, missing: " + message);
	}
}
