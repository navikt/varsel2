package no.nav.varsel.service.support.exception;

/**
 * Funksjonell exception som benyttes når varselbestillingen ikke er testvarselbestilling og samtidig har inaktiv varselmal.
 *
 * @author Lars Aune
 */
public class VarselbestillingInaktivVarselmalException extends FunctionalVarselException {
	public VarselbestillingInaktivVarselmalException(String varselbestillingId, String varseltypeId) {
		super("Varselbestilling med id " + varselbestillingId + " bruker inaktiv varselmal med id " + varseltypeId + ".");
	}
}
