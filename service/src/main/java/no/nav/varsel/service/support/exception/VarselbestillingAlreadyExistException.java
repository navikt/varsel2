package no.nav.varsel.service.support.exception;

import java.time.LocalDate;

/**
 * Exception for varselbestilling that already exists
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
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
