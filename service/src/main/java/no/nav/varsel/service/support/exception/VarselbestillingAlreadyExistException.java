package no.nav.varsel.service.support.exception;

/**
 * Exception for varselbestilling that already exists
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingAlreadyExistException extends FunctionalVarselException {

	public VarselbestillingAlreadyExistException(String varselbestillingId) {
		super(String.format("Varselbestilling with varselbestillingId=%s does already exist", varselbestillingId));
	}
}
