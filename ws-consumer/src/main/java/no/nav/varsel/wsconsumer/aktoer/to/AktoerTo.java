package no.nav.varsel.wsconsumer.aktoer.to;

import no.nav.varsel.domain.to.MottakerType;

/**
 * To for Aktoer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerTo {

	private String ident;
	private MottakerType mottakerType;

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public MottakerType getMottakerType() {
		return mottakerType;
	}

	public void setMottakerType(MottakerType mottakerType) {
		this.mottakerType = mottakerType;
	}
}
