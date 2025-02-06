package no.nav.varsel.exception.functional;

public class InvalidVarselStatusException extends FunctionalVarselException {

	public InvalidVarselStatusException(String varselId, String status) {
		super(String.format("Varsel with varselId=%s has invalid statusCode=%s", varselId, status));
	}
}
