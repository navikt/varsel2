package no.nav.varsel.service.support.exception.functional;

public class VarselbestillingNotExistException extends FunctionalVarselException {

	public VarselbestillingNotExistException(String varselbestillingId) {
		super(String.format("Varselbestilling with varselbestillingId=%s does not exist", varselbestillingId));
	}
}
