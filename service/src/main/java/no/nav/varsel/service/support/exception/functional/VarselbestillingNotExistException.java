package no.nav.varsel.service.support.exception.functional;

public class VarselbestillingNotExistException extends FunctionalVarselException {

	public VarselbestillingNotExistException(String varselbestillingId) {
		super(String.format("Varselbestilling med varselbestillingId=%s finnes ikke.", varselbestillingId));
	}
}
