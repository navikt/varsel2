package no.nav.varsel.service.support.exception;

import no.nav.varsel.domain.object.Varsel;

/**
 * Exception thrown when a Varsel has an invalid status
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class InvalidVarselStatusException extends FunctionalException {

	public InvalidVarselStatusException(Varsel varsel) {
		super("Varsel with varselId=" + (varsel != null ? varsel.getVarselId() : null) +
				" has invalid statusCode=" + (varsel != null ? varsel.getStatus() : null));
	}
}
