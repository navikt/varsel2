package no.nav.varsel.service.tvarsel005.to;

import java.time.LocalDateTime;

/**
 * @author Lars Aune
 */
public class VarselTo {
	private String kanal;
	private LocalDateTime sendtTidspunkt;
	private LocalDateTime distribusjonsTidspunkt;
	private String kontaktInfo;
	private String varselTittel;
	private String varselTekst;
	private String varselURL;
	private boolean revarsel;

	public String getKanal() {
		return kanal;
	}

	private void setKanal(String kanal) {
		this.kanal = kanal;
	}

	public LocalDateTime getSendtTidspunkt() {
		return sendtTidspunkt;
	}

	private void setSendtTidspunkt(LocalDateTime sendtTidspunkt) {
		this.sendtTidspunkt = sendtTidspunkt;
	}

	public LocalDateTime getDistribusjonsTidspunkt() {
		return distribusjonsTidspunkt;
	}

	private void setDistribusjonsTidspunkt(LocalDateTime distribusjonsTidspunkt) {
		this.distribusjonsTidspunkt = distribusjonsTidspunkt;
	}

	public String getKontaktInfo() {
		return kontaktInfo;
	}

	private void setKontaktInfo(String kontaktInfo) {
		this.kontaktInfo = kontaktInfo;
	}

	public String getVarselTittel() {
		return varselTittel;
	}

	private void setVarselTittel(String varselTittel) {
		this.varselTittel = varselTittel;
	}

	public String getVarselTekst() {
		return varselTekst;
	}

	private void setVarselTekst(String varselTekst) {
		this.varselTekst = varselTekst;
	}

	public String getVarselURL() {
		return varselURL;
	}

	private void setVarselURL(String varselURL) {
		this.varselURL = varselURL;
	}

	public boolean isRevarsel() {
		return revarsel;
	}

	private void setRevarsel(boolean revarsel) {
		this.revarsel = revarsel;
	}

	public static final class Builder {

		private String kanal;
		private LocalDateTime sendtTidspunkt;
		private LocalDateTime distribusjonTidspunkt;
		private String kontaktInfo;
		private String varselTittel;
		private String varselTekst;
		private String varselURL;
		private boolean revarsel;

		public VarselTo build() {
			VarselTo result = new VarselTo();
			result.setKanal(this.kanal);
			result.setSendtTidspunkt(this.sendtTidspunkt);
			result.setDistribusjonsTidspunkt(this.distribusjonTidspunkt);
			result.setKontaktInfo(this.kontaktInfo);
			result.setVarselTittel(this.varselTittel);
			result.setVarselTekst(this.varselTekst);
			result.setVarselURL(this.varselURL);
			result.setRevarsel(this.revarsel);
			return result;
		}

		public static VarselTo.Builder aVarselTo() {
			return new VarselTo.Builder();
		}

		public Builder kanal(String kanal) {
			this.kanal = kanal;
			return this;
		}

		public Builder sendtTidspunkt(LocalDateTime sendtTidspunkt) {
			this.sendtTidspunkt = sendtTidspunkt;
			return this;
		}

		public Builder distribusjonTidspunkt(LocalDateTime distribusjonTidspunkt) {
			this.distribusjonTidspunkt = distribusjonTidspunkt;
			return this;
		}

		public Builder kontaktInfo(String kontaktInfo) {
			this.kontaktInfo = kontaktInfo;
			return this;
		}

		public Builder varselTittel(String varselTittel) {
			this.varselTittel = varselTittel;
			return this;
		}

		public Builder varselTekst(String varselTekst) {
			this.varselTekst = varselTekst;
			return this;
		}


		public Builder varselURL(String varselURL) {
			this.varselURL = varselURL;
			return this;
		}

		public Builder revarsel(boolean revarsel){
			this.revarsel = revarsel;
			return this;
		}
	}
}
