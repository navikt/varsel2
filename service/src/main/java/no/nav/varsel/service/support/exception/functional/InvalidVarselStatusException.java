package no.nav.varsel.service.support.exception.functional;

/**
 * Exception thrown when a Varsel has an invalid status
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class InvalidVarselStatusException extends FunctionalVarselException {

	public InvalidVarselStatusException(String varselId, String status) {
		super(String.format("Varsel with varselId=%s has invalid statusCode=%s", varselId, status));
	}
}
