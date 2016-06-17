package no.nav.varsel.service.support.exception;

/**
 * Exception for varselbestilling that does not exist
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingNotExistException extends FunctionalVarselException {

	public VarselbestillingNotExistException(String varselbestillingId) {
		super(String.format("Varselbestilling with varselbestillingId=%s does not exist", varselbestillingId));
	}
}
