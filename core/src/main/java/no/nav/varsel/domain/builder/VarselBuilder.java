package no.nav.varsel.domain.builder;

import no.nav.varsel.domain.auxiliary.Builder;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;

import java.time.LocalDateTime;

public final class VarselBuilder extends Builder<Varsel> {

	private Long id;
	private Varselbestilling varselbestilling;
	private String varselId;
	private KanalCode kanal;
	private LocalDateTime sendtTidspunkt;
	private LocalDateTime distribusjonTidspunkt;
	private LocalDateTime kvitteringTidspunkt;
	private String kontaktInfo;
	private StatusCode status;
	private String feilbeskrivelse;
	private String varselTittel;
	private String varselTekst;
	private String varselUrl;
	private Boolean erRevarsel;

	private VarselBuilder() {
	}

	public static VarselBuilder aVarsel() {
		return new VarselBuilder();
	}

	public VarselBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public VarselBuilder varselbestilling(Varselbestilling varselbestilling) {
		this.varselbestilling = varselbestilling;
		return this;
	}

	public VarselBuilder varselId(String varselId) {
		this.varselId = varselId;
		return this;
	}

	public VarselBuilder kanal(KanalCode kanal) {
		this.kanal = kanal;
		return this;
	}

	public VarselBuilder sendtTidspunkt(LocalDateTime sendtTidspunkt) {
		this.sendtTidspunkt = sendtTidspunkt;
		return this;
	}

	public VarselBuilder distribusjonTidspunkt(LocalDateTime distribusjonTidspunkt) {
		this.distribusjonTidspunkt = distribusjonTidspunkt;
		return this;
	}

	public VarselBuilder kvitteringTidspunkt(LocalDateTime kvitteringTidspunkt) {
		this.kvitteringTidspunkt = kvitteringTidspunkt;
		return this;
	}

	public VarselBuilder kontaktInfo(String kontaktInfo) {
		this.kontaktInfo = kontaktInfo;
		return this;
	}

	public VarselBuilder status(StatusCode status) {
		this.status = status;
		return this;
	}

	public VarselBuilder feilbeskrivelse(String feilbeskrivelse) {
		this.feilbeskrivelse = feilbeskrivelse;
		return this;
	}

	public VarselBuilder varselTittel(String varselTittel) {
		this.varselTittel = varselTittel;
		return this;
	}

	public VarselBuilder varselTekst(String varselTekst) {
		this.varselTekst = varselTekst;
		return this;
	}

	public VarselBuilder varselUrl(String varselUrl) {
		this.varselUrl = varselUrl;
		return this;
	}

	public VarselBuilder erRevarsel(Boolean erRevarsel) {
		this.erRevarsel = erRevarsel;
		return this;
	}

	public Varsel build() {
		Varsel varsel = new Varsel();
		varsel.setId(id);
		varsel.setVarselbestilling(varselbestilling);
		varsel.setVarselId(varselId);
		varsel.setKanal(kanal);
		varsel.setSendtTidspunkt(sendtTidspunkt);
		varsel.setDistribusjonTidspunkt(distribusjonTidspunkt);
		varsel.setKvitteringTidspunkt(kvitteringTidspunkt);
		varsel.setKontaktInfo(kontaktInfo);
		varsel.setStatus(status);
		varsel.setFeilbeskrivelse(feilbeskrivelse);
		varsel.setVarselTittel(varselTittel);
		varsel.setVarselTekst(varselTekst);
		varsel.setVarselUrl(varselUrl);
		varsel.setErRevarsel(erRevarsel);
		return varsel;
	}
}
