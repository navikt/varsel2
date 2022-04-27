package no.nav.varsel.service.support;

import lombok.ToString;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.to.AktoerTo;

import java.time.LocalDateTime;

/**
 * To for varselutsending
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@ToString
public class VarselutsendingTo {
	private LocalDateTime utloepstidspunkt;
	private String varseltypeId;
	private KanalCode kanal;
	private AktoerTo mottaker;
	private String varselId;
	private String varselUrl;
	private String varselTekst;
	private String varselTittel;
	private String kontaktInformasjon;

	public LocalDateTime getUtloepstidspunkt() {
		return utloepstidspunkt;
	}

	public void setUtloepstidspunkt(LocalDateTime utloepstidspunkt) {
		this.utloepstidspunkt = utloepstidspunkt;
	}

	public String getVarseltypeId() {
		return varseltypeId;
	}

	public void setVarseltypeId(String varseltypeId) {
		this.varseltypeId = varseltypeId;
	}

	public KanalCode getKanal() {
		return kanal;
	}

	public void setKanal(KanalCode kanal) {
		this.kanal = kanal;
	}

	public AktoerTo getMottaker() {
		return mottaker;
	}

	public void setMottaker(AktoerTo mottaker) {
		this.mottaker = mottaker;
	}

	public String getVarselId() {
		return varselId;
	}

	public void setVarselId(String varselId) {
		this.varselId = varselId;
	}

	public String getVarselUrl() {
		return varselUrl;
	}

	public void setVarselUrl(String varselUrl) {
		this.varselUrl = varselUrl;
	}

	public String getVarselTekst() {
		return varselTekst;
	}

	public void setVarselTekst(String varselTekst) {
		this.varselTekst = varselTekst;
	}

	public String getVarselTittel() {
		return varselTittel;
	}

	public void setVarselTittel(String varselTittel) {
		this.varselTittel = varselTittel;
	}

	public String getKontaktInformasjon() {
		return kontaktInformasjon;
	}

	public void setKontaktInformasjon(String kontaktInformasjon) {
		this.kontaktInformasjon = kontaktInformasjon;
	}

	public static final class VarselutsendingToBuilder {
		private LocalDateTime utloepstidspunkt;
		private String varseltypeId;
		private KanalCode kanal;
		private AktoerTo mottaker;
		private String varselId;
		private String varselUrl;
		private String varselTekst;
		private String varselTittel;
		private String kontaktInformasjon;

		private VarselutsendingToBuilder() {
		}

		public static VarselutsendingToBuilder aVarselutsendingTo() {
			return new VarselutsendingToBuilder();
		}

		public VarselutsendingToBuilder utloepstidspunkt(LocalDateTime utloepstidspunkt) {
			this.utloepstidspunkt = utloepstidspunkt;
			return this;
		}

		public VarselutsendingToBuilder varseltypeId(String varseltypeId) {
			this.varseltypeId = varseltypeId;
			return this;
		}

		public VarselutsendingToBuilder kanal(KanalCode kanal) {
			this.kanal = kanal;
			return this;
		}

		public VarselutsendingToBuilder mottaker(AktoerTo mottaker) {
			this.mottaker = mottaker;
			return this;
		}

		public VarselutsendingToBuilder varselId(String varselId) {
			this.varselId = varselId;
			return this;
		}

		public VarselutsendingToBuilder varselUrl(String varselUrl) {
			this.varselUrl = varselUrl;
			return this;
		}

		public VarselutsendingToBuilder varselTekst(String varselTekst) {
			this.varselTekst = varselTekst;
			return this;
		}

		public VarselutsendingToBuilder varselTittel(String varselTittel) {
			this.varselTittel = varselTittel;
			return this;
		}

		public VarselutsendingToBuilder kontaktInformasjon(String kontaktInformasjon) {
			this.kontaktInformasjon = kontaktInformasjon;
			return this;
		}

		public VarselutsendingTo build() {
			VarselutsendingTo varselutsendingTo = new VarselutsendingTo();
			varselutsendingTo.setUtloepstidspunkt(utloepstidspunkt);
			varselutsendingTo.setVarseltypeId(varseltypeId);
			varselutsendingTo.setKanal(kanal);
			varselutsendingTo.setMottaker(mottaker);
			varselutsendingTo.setVarselId(varselId);
			varselutsendingTo.setVarselUrl(varselUrl);
			varselutsendingTo.setVarselTekst(varselTekst);
			varselutsendingTo.setVarselTittel(varselTittel);
			varselutsendingTo.setKontaktInformasjon(kontaktInformasjon);
			return varselutsendingTo;
		}
	}
}
