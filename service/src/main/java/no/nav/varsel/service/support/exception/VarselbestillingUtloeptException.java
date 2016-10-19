package no.nav.varsel.service.support.exception;


import java.time.LocalDateTime;

/**
 * Exception for when a varsel is past its expiration
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingUtloeptException extends FunctionalVarselException {
	public VarselbestillingUtloeptException(String varselbestillingId, LocalDateTime utloept) {
		super(String.format("Varselbestilling with varselbestillingId=%s has utloepstidspunkt=%s", varselbestillingId, utloept.toString()));
	}
}
