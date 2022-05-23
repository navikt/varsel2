package no.nav.varsel.service.support.exception.functional;

/**
 * Exception thrown when a Varsel has an invalid status
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class StatusmeldingValidationException extends FunctionalVarselException {

	public StatusmeldingValidationException(String message) {
		super(message);
	}
}
