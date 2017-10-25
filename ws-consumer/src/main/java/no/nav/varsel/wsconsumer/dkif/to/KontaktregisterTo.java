package no.nav.varsel.wsconsumer.dkif.to;

import no.nav.varsel.domain.code.KanalCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * To object for DigitalKontaktinformasjon
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class KontaktregisterTo implements Serializable {

	private static final long serialVersionUID = ***gammelt_fnr***8148963L;

	public static final int DATE_VALID_MONTHS = 18;

	private boolean reservasjon;
	private String epostadresse;
	private LocalDateTime epostSistOppdatert;
	private LocalDateTime epostSistVerifisert;
	private String mobiltelefonnummer;
	private LocalDateTime mobiltelefonSistOppdatert;
	private LocalDateTime mobiltelefonSistVerifisert;
	private String kontaktInfo;
	private Collection<KanalCode> kanaler;

	public boolean isReservasjon() {
		return reservasjon;
	}

	public void setReservasjon(boolean reservasjon) {
		this.reservasjon = reservasjon;
	}

	public String getEpostadresse() {
		return epostadresse;
	}

	public void setEpostadresse(String epostadresse) {
		this.epostadresse = epostadresse;
	}

	public LocalDateTime getEpostSistOppdatert() {
		return epostSistOppdatert;
	}

	public void setEpostSistOppdatert(LocalDateTime epostSistOppdatert) {
		this.epostSistOppdatert = epostSistOppdatert;
	}

	public LocalDateTime getEpostSistVerifisert() {
		return epostSistVerifisert;
	}

	public void setEpostSistVerifisert(LocalDateTime epostSistVerifisert) {
		this.epostSistVerifisert = epostSistVerifisert;
	}

	public String getMobiltelefonnummer() {
		return mobiltelefonnummer;
	}

	public void setMobiltelefonnummer(String mobiltelefonnummer) {
		this.mobiltelefonnummer = mobiltelefonnummer;
	}

	public LocalDateTime getMobiltelefonSistOppdatert() {
		return mobiltelefonSistOppdatert;
	}

	public void setMobiltelefonSistOppdatert(LocalDateTime mobiltelefonSistOppdatert) {
		this.mobiltelefonSistOppdatert = mobiltelefonSistOppdatert;
	}

	public LocalDateTime getMobiltelefonSistVerifisert() {
		return mobiltelefonSistVerifisert;
	}

	public void setMobiltelefonSistVerifisert(LocalDateTime mobiltelefonSistVerifisert) {
		this.mobiltelefonSistVerifisert = mobiltelefonSistVerifisert;
	}

	public String getKontaktInfo() {
		return kontaktInfo;
	}

	public void setKontaktInfo(String kontaktInfo) {
		this.kontaktInfo = kontaktInfo;
	}

	public Collection<KanalCode> getKanaler() {
		return kanaler;
	}

	public void setKanaler(Collection<KanalCode> kanaler) {
		this.kanaler = kanaler;
	}

	public boolean isEpostDateInvalid() {
		return isInvalid(getEpostSistOppdatert()) && isInvalid(getEpostSistVerifisert());
	}

	public boolean isMobilDateInvalid() {
		return isInvalid(getMobiltelefonSistOppdatert()) && isInvalid(getMobiltelefonSistVerifisert());
	}

	private boolean isInvalid(LocalDateTime dateTime) {
		return dateTime == null || LocalDate.now().minusMonths(DATE_VALID_MONTHS).isAfter(dateTime.toLocalDate());
	}

	public void cleanExpiredInfo() {
		if (isMobilDateInvalid()) {
			setMobiltelefonnummer(null);
		}
		if (isEpostDateInvalid()) {
			setEpostadresse(null);
		}
	}

	public static final class KontaktregisterToBuilder {
		private boolean reservasjon;
		private String epostadresse;
		private LocalDateTime epostSistOppdatert;
		private LocalDateTime epostSistVerifisert;
		private String mobiltelefonnummer;
		private LocalDateTime mobiltelefonSistOppdatert;
		private LocalDateTime mobiltelefonSistVerifisert;
		private String kontaktInfo;
		private Collection<KanalCode> kanaler;

		private KontaktregisterToBuilder() {
			//Avoid public instantiation
		}

		public static KontaktregisterToBuilder aKontaktregisterTo() {
			return new KontaktregisterToBuilder();
		}

		public KontaktregisterToBuilder reservasjon(boolean reservasjon) {
			this.reservasjon = reservasjon;
			return this;
		}

		public KontaktregisterToBuilder epostadresse(String epostadresse) {
			this.epostadresse = epostadresse;
			return this;
		}

		public KontaktregisterToBuilder epostSistOppdatert(LocalDateTime epostSistOppdatert) {
			this.epostSistOppdatert = epostSistOppdatert;
			return this;
		}

		public KontaktregisterToBuilder epostSistVerifisert(LocalDateTime epostSistVerifisert) {
			this.epostSistVerifisert = epostSistVerifisert;
			return this;
		}

		public KontaktregisterToBuilder mobiltelefonnummer(String mobiltelefonnummer) {
			this.mobiltelefonnummer = mobiltelefonnummer;
			return this;
		}

		public KontaktregisterToBuilder mobiltelefonSistOppdatert(LocalDateTime mobiltelefonSistOppdatert) {
			this.mobiltelefonSistOppdatert = mobiltelefonSistOppdatert;
			return this;
		}

		public KontaktregisterToBuilder mobiltelefonSistVerifisert(LocalDateTime mobiltelefonSistVerifisert) {
			this.mobiltelefonSistVerifisert = mobiltelefonSistVerifisert;
			return this;
		}

		public KontaktregisterToBuilder kontaktInfo(String kontaktInfo) {
			this.kontaktInfo = kontaktInfo;
			return this;
		}

		public KontaktregisterToBuilder kanaler(Collection<KanalCode> kanaler) {
			this.kanaler = kanaler;
			return this;
		}

		public KontaktregisterTo build() {
			KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();
			kontaktregisterTo.setReservasjon(reservasjon);
			kontaktregisterTo.setEpostadresse(epostadresse);
			kontaktregisterTo.setEpostSistOppdatert(epostSistOppdatert);
			kontaktregisterTo.setEpostSistVerifisert(epostSistVerifisert);
			kontaktregisterTo.setMobiltelefonnummer(mobiltelefonnummer);
			kontaktregisterTo.setMobiltelefonSistOppdatert(mobiltelefonSistOppdatert);
			kontaktregisterTo.setMobiltelefonSistVerifisert(mobiltelefonSistVerifisert);
			kontaktregisterTo.setKontaktInfo(kontaktInfo);
			kontaktregisterTo.setKanaler(kanaler);
			return kontaktregisterTo;
		}
	}
}
