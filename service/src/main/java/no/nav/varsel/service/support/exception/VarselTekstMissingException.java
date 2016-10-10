package no.nav.varsel.service.support.exception;

/**
 * Exception for manglende revarslingstekst
 *
 * @author Paul Magne Lunde, Visma Consulting
 */
public class VarselTekstMissingException extends FunctionalVarselException {
	public VarselTekstMissingException(String message) {
		super(message);
	}
}
