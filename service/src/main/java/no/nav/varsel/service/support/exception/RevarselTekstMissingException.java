package no.nav.varsel.service.support.exception;

/**
 * Exception for manglende revarslingstekst
 *
 * @author Paul Magne Lunde, Visma Consulting
 */
public class RevarselTekstMissingException extends FunctionalVarselException {
	public RevarselTekstMissingException(String message) {
		super(message);
	}
}
