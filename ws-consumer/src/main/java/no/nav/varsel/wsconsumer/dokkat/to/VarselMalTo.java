package no.nav.varsel.wsconsumer.dokkat.to;

import no.nav.varsel.domain.code.KanalCode;

/**
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselMalTo {
	private KanalCode kanal;
	private String tittel;
	private String foerstegangsTekst;
	private String revarslingTekst;

	public KanalCode getKanal() {
		return kanal;
	}

	public void setKanal(KanalCode kanal) {
		this.kanal = kanal;
	}

	public String getTittel() {
		return tittel;
	}

	public void setTittel(String tittel) {
		this.tittel = tittel;
	}

	public String getFoerstegangsTekst() {
		return foerstegangsTekst;
	}

	public void setFoerstegangsTekst(String foerstegangsTekst) {
		this.foerstegangsTekst = foerstegangsTekst;
	}

	public String getRevarslingTekst() {
		return revarslingTekst;
	}

	public void setRevarslingTekst(String revarslingTekst) {
		this.revarslingTekst = revarslingTekst;
	}
}
