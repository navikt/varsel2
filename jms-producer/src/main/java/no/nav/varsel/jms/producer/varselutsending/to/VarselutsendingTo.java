package no.nav.varsel.jms.producer.varselutsending.to;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.to.AktoerTo;

import java.time.LocalDateTime;

/**
 * To for varselutsending
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingTo {
	private LocalDateTime utloepstidspunkt;
	private String varslingstype;
	private KanalCode kanal;
	private AktoerTo mottaker;
	private String varselId;
	private String varselUrl;
	private String varselTekst;
	private String varselTittel;

	public LocalDateTime getUtloepstidspunkt() {
		return utloepstidspunkt;
	}

	public void setUtloepstidspunkt(LocalDateTime utloepstidspunkt) {
		this.utloepstidspunkt = utloepstidspunkt;
	}

	public String getVarslingstype() {
		return varslingstype;
	}

	public void setVarslingstype(String varslingstype) {
		this.varslingstype = varslingstype;
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
}
