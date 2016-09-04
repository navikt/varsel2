package no.nav.varsel.service.support.exception;

/**
 * Funksjonell exception som benyttes når varselbestillingen ikke er testvarselbestilling og samtidig har inaktiv varselmal.
 *
 * @author Lars Aune
 */
public class VarselInaktivVarselmalException extends FunctionalVarselException {
	public VarselInaktivVarselmalException(String mottakerId, String varseltypeId) {
		super("Mottaker med id " + mottakerId + " bruker inaktiv varselmal med id " + varseltypeId + ".");
	}
}
