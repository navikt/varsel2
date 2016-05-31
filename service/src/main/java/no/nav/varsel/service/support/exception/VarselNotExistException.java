package no.nav.varsel.service.support.exception;

/**
 * Exception thrown when Varsel with a given varselId not exists
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class VarselNotExistException extends FunctionalException {

	public VarselNotExistException(String varselId) {
		super("Varsel with varselId=" + varselId + " does not exist");
	}


}
