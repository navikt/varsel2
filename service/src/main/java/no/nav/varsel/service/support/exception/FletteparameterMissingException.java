package no.nav.varsel.service.support.exception;

/**
 * Exception for fletting where a parameter is missing
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class FletteparameterMissingException extends FunctionalVarselException {
	public FletteparameterMissingException(String message) {
		super("Not all parameters given for varsel, missing: " + message);
	}
}
