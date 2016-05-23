package no.nav.varsel.domain.object;

import no.nav.varsel.domain.auxillary.AbstractDomainObject;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Domain object for Varsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Entity
@Table(name = "VARSEL")
public class Varsel extends AbstractDomainObject {

	private static final long serialVersionUID = 7416687143L;

	private static final String VARSEL_SEQ = "VARSEL_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VARSEL_SEQ)
	@SequenceGenerator(name = VARSEL_SEQ, sequenceName = VARSEL_SEQ, allocationSize = 100)
	@Column(name = "id", updatable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(nullable = false, name = "fk_varselbestilling_id")
	private Varselbestilling varselbestilling;

	@Column(name = "varsel_id", nullable = false)
	private String varselId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_kanal", nullable = false)
	private KanalCode kanal;

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	@Column(name = "sendt_tidspunkt", columnDefinition = "TIMESTAMP")
	private LocalDateTime sendtTidspunkt;

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	@Column(name = "distribusjon_tidspunkt", columnDefinition = "TIMESTAMP")
	private LocalDateTime distribusjonTidspunkt;

	@Column(name = "kontakt_info", nullable = false)
	private String kontaktInfo;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_status", nullable = false)
	private StatusCode status;

	@Column(name = "feilbeskrivelse")
	private String feilbeskrivelse;

	@Column(name = "varsel_tittel")
	private String varselTittel;

	@Column(name = "varsel_tekst", nullable = false)
	private String varselTekst;

	@Column(name = "varsel_url")
	private String varselUrl;

	public Varsel(Long id, Long versjon) {
		this.id = id;
		setVersion(versjon);
	}

	public Varsel() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Varselbestilling getVarselbestilling() {
		return varselbestilling;
	}

	public void setVarselbestilling(Varselbestilling varselbestilling) {
		this.varselbestilling = varselbestilling;
	}

	public String getVarselId() {
		return varselId;
	}

	public void setVarselId(String varselId) {
		this.varselId = varselId;
	}

	public KanalCode getKanal() {
		return kanal;
	}

	public void setKanal(KanalCode kanal) {
		this.kanal = kanal;
	}

	public LocalDateTime getSendtTidspunkt() {
		return sendtTidspunkt;
	}

	public void setSendtTidspunkt(LocalDateTime sendtTidspunkt) {
		this.sendtTidspunkt = sendtTidspunkt;
	}

	public LocalDateTime getDistribusjonTidspunkt() {
		return distribusjonTidspunkt;
	}

	public void setDistribusjonTidspunkt(LocalDateTime distribusjonTidspunkt) {
		this.distribusjonTidspunkt = distribusjonTidspunkt;
	}

	public String getKontaktInfo() {
		return kontaktInfo;
	}

	public void setKontaktInfo(String kontaktInfo) {
		this.kontaktInfo = kontaktInfo;
	}

	public StatusCode getStatus() {
		return status;
	}

	public void setStatus(StatusCode status) {
		this.status = status;
	}

	public String getFeilbeskrivelse() {
		return feilbeskrivelse;
	}

	public void setFeilbeskrivelse(String feilbeskrivelse) {
		this.feilbeskrivelse = feilbeskrivelse;
	}

	public String getVarselTittel() {
		return varselTittel;
	}

	public void setVarselTittel(String varselTittel) {
		this.varselTittel = varselTittel;
	}

	public String getVarselTekst() {
		return varselTekst;
	}

	public void setVarselTekst(String varselTekst) {
		this.varselTekst = varselTekst;
	}

	public String getVarselUrl() {
		return varselUrl;
	}

	public void setVarselUrl(String varselUrl) {
		this.varselUrl = varselUrl;
	}
}
