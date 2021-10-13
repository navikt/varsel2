package no.nav.varsel.wsconsumer.pdl.support;

public class PdlFunctionalException extends FunctionalVarselException {
    public PdlFunctionalException(String message) {
        super(message);
    }

    public PdlFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
