package no.nav.varsel.service.support.exception;

/**
 * Funksjonell exception som benyttes når varselbestillingen ikke er testvarselbestilling og samtidig har inaktiv varselmal.
 *
 * @author Lars Aune
 */
public class VarselInaktivVarselmalException extends FunctionalVarselException {
	public VarselInaktivVarselmalException(String mottakerId, String varseltypeId, String bestillingId) {
		super("Det er ikke mulig å bestille servicemelding for mottaker med mottakerId=" + mottakerId +
				" og bestillingId=" + bestillingId  +
				" med inaktiv varselmal med varseltypeId=" + varseltypeId + ".");
	}
}
