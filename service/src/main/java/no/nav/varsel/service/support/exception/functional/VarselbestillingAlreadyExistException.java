package no.nav.varsel.service.support.exception.functional;

import java.time.LocalDate;

public class VarselbestillingAlreadyExistException extends FunctionalVarselException {

	public VarselbestillingAlreadyExistException(String varselbestillingId) {
		super(String.format("Varselbestilling with varselbestillingId=%s does already exist", varselbestillingId));
	}

	public VarselbestillingAlreadyExistException(
			String varselbestillingId, Integer antallRevarslinger, LocalDate nesteVarslingDato) {
		super(String.format("Varselbestilling with varselbestillingId=%s already sendt, " +
				"antallRevarslinger=%d, nesteVarslingDato=%s", varselbestillingId, antallRevarslinger, nesteVarslingDato));
	}
}
