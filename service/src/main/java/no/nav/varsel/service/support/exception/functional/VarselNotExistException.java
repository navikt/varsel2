package no.nav.varsel.service.support.exception.functional;

public class VarselNotExistException extends FunctionalVarselException {

	public VarselNotExistException(String varselId) {
		super("Varsel with varselId=" + varselId + " does not exist");
	}


}
