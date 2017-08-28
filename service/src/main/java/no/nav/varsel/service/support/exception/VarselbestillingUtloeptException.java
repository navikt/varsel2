package no.nav.varsel.service.support.exception;


import java.time.LocalDateTime;

/**
 * Exception for when a varsel is past its expiration
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingUtloeptException extends FunctionalVarselException {
	public VarselbestillingUtloeptException(String varselbestillingId, LocalDateTime utloept) {
		super(message(varselbestillingId, utloept));
	}

	protected static String message(String varselbestillingId, LocalDateTime utloept) {
		String id = varselbestillingId == null ? "" : " with varselbestillingId=" + varselbestillingId;
		return String.format("Varselbestilling%s has utloepstidspunkt=%s", id, utloept.toString());
	}
}
