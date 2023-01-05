package no.nav.varsel.service.support.exception.functional;


import java.time.LocalDateTime;

public class VarselbestillingUtloeptException extends FunctionalVarselException {
	public VarselbestillingUtloeptException(String varselbestillingId, LocalDateTime utloept) {
		super(message(varselbestillingId, utloept));
	}

	protected static String message(String varselbestillingId, LocalDateTime utloept) {
		String id = varselbestillingId == null ? "" : " with varselbestillingId=" + varselbestillingId;
		return String.format("Varselbestilling%s has utloepstidspunkt=%s", id, utloept.toString());
	}
}
