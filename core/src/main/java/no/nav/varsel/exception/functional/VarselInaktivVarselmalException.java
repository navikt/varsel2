package no.nav.varsel.exception.functional;

public class VarselInaktivVarselmalException extends FunctionalVarselException {
	public VarselInaktivVarselmalException(String mottakerId, String varseltypeId, String bestillingId) {
		super("Det er ikke mulig å bestille servicemelding for mottaker med mottakerId=" + mottakerId +
				" og bestillingId=" + bestillingId  +
				" med inaktiv varselmal med varseltypeId=" + varseltypeId + ".");
	}
}
