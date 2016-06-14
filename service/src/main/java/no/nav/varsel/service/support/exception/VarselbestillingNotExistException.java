package no.nav.varsel.service.support.exception;

/**
 * Exception thrown when Varselbestilling with a given varselbestillingId not exists
 * @author Hiep Luong Nguyen, Computas
 */
public class VarselbestillingNotExistException extends FunctionalVarselException {
    public VarselbestillingNotExistException(String varselbestillingId) {
        super("Varselbestilling with varselbestillingId=" + varselbestillingId + " does not exist");
    }
}
