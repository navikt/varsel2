package no.nav.varsel.wsconsumer.dokkat.to;

import no.nav.varsel.domain.code.KanalCode;

import java.util.Set;

/**
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoTo {
	private String preferertKanal;
	private String varselURL;
	private Set<VarselMalTo> maler;

	public String getPreferertKanal() {
		return preferertKanal;
	}

	public void setPreferertKanal(String preferertKanal) {
		this.preferertKanal = preferertKanal;
	}

	public Set<VarselMalTo> getMaler() {
		return maler;
	}

	public void setMaler(Set<VarselMalTo> maler) {
		this.maler = maler;
	}

	public String getVarselURL() {
		return varselURL;
	}

	public void setVarselURL(String varselURL) {
		this.varselURL = varselURL;
	}

	public VarselMalTo getMal(KanalCode kanalCode) {
		return maler.stream().filter(m -> m.getKanal() == kanalCode).findFirst().get();
	}
}
