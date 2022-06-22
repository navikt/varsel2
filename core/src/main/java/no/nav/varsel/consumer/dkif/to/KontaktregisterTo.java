package no.nav.varsel.consumer.dkif.to;

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

	private static final long serialVersionUID = 373780690208148963L;

	public static final int DATE_VALID_MONTHS = 18;

	private boolean reservasjon;
	private String epostadresse;
	private String mobiltelefonnummer;
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

	public String getMobiltelefonnummer() {
		return mobiltelefonnummer;
	}

	public void setMobiltelefonnummer(String mobiltelefonnummer) {
		this.mobiltelefonnummer = mobiltelefonnummer;
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

	private boolean isInvalid(LocalDateTime dateTime) {
		return dateTime == null || LocalDate.now().minusMonths(DATE_VALID_MONTHS).isAfter(dateTime.toLocalDate());
	}

	public static final class KontaktregisterToBuilder {
		private boolean reservasjon;
		private String epostadresse;
		private String mobiltelefonnummer;
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

		public KontaktregisterToBuilder mobiltelefonnummer(String mobiltelefonnummer) {
			this.mobiltelefonnummer = mobiltelefonnummer;
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
			kontaktregisterTo.setMobiltelefonnummer(mobiltelefonnummer);
			kontaktregisterTo.setKontaktInfo(kontaktInfo);
			kontaktregisterTo.setKanaler(kanaler);
			return kontaktregisterTo;
		}
	}
}
