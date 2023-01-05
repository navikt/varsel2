package no.nav.varsel.consumer.dokkat.to;

import no.nav.varsel.domain.code.KanalCode;

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

	public static final class VarselMalToBuilder {
		private KanalCode kanal;
		private String tittel;
		private String foerstegangsTekst;
		private String revarslingTekst;

		private VarselMalToBuilder() {
		}

		public static VarselMalToBuilder aVarselMalTo() {
			return new VarselMalToBuilder();
		}

		public VarselMalToBuilder kanal(KanalCode kanal) {
			this.kanal = kanal;
			return this;
		}

		public VarselMalToBuilder tittel(String tittel) {
			this.tittel = tittel;
			return this;
		}

		public VarselMalToBuilder foerstegangsTekst(String foerstegangsTekst) {
			this.foerstegangsTekst = foerstegangsTekst;
			return this;
		}

		public VarselMalToBuilder revarslingTekst(String revarslingTekst) {
			this.revarslingTekst = revarslingTekst;
			return this;
		}

		public VarselMalTo build() {
			VarselMalTo varselMalTo = new VarselMalTo();
			varselMalTo.setKanal(kanal);
			varselMalTo.setTittel(tittel);
			varselMalTo.setFoerstegangsTekst(foerstegangsTekst);
			varselMalTo.setRevarslingTekst(revarslingTekst);
			return varselMalTo;
		}
	}
}
