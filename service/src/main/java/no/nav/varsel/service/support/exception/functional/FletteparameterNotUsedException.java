package no.nav.varsel.service.support.exception.functional;

/**
 * Exception for fletting where a parameter is not used
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class FletteparameterNotUsedException extends FunctionalVarselException {
	public FletteparameterNotUsedException(String message) {
		super("Not all parameters used for varsel, unused: " + message);
	}
}
