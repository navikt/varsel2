package no.nav.varsel.wsconsumer.dkif.to;

import no.nav.varsel.domain.code.KanalCode;
import org.joda.time.LocalDateTime;

import java.util.Arrays;
import java.util.Collection;

/**
 * To for DigitalKontaktinformasjon
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class DigitalKontaktinfoTo {

	private String personident;
	private String reservasjon;
	private String epostadresse;
	private LocalDateTime epostSistOppdatert;
	private LocalDateTime epostSistVerifisert;
	private String mobiltelefonnummer;
	private LocalDateTime mobiltelefonSistOppdatert;
	private LocalDateTime mobiltelefonSistVerifisert;
	private String kontaktInfo;
	private Collection<KanalCode> kanaler;

	public String getPersonident() {
		return personident;
	}

	public void setPersonident(String personident) {
		this.personident = personident;
	}

	public String getReservasjon() {
		return reservasjon;
	}

	public void setReservasjon(String reservasjon) {
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
}
