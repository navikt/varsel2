package no.nav.varsel.domain.object;

import no.nav.varsel.domain.auxiliary.AbstractDomainObject;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Domain object for Varselbestilling
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Entity
@Table(name = "VARSELBESTILLING")
public class Varselbestilling extends AbstractDomainObject {

	private static final long serialVersionUID = ***gammelt_fnr***412768L;

	private static final String VARSELBESTILLING_SEQ = "VARSELBESTILLING_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VARSELBESTILLING_SEQ)
	@SequenceGenerator(name = VARSELBESTILLING_SEQ, sequenceName = VARSELBESTILLING_SEQ, allocationSize = 100)
	@Column(name = "id", updatable = false)
	private Long id;

	@Column(name = "varselbestilling_id", nullable = false)
	private String varselbestillingId;

	@Column(name = "varslingstype", nullable = false)
	private String varslingstype;

	@Column(name = "preferert_kanal")
	private String preferertKanal;

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	@Column(name = "utlop_tidspunkt", columnDefinition = "TIMESTAMP")
	private LocalDateTime utlopTidspunkt;

	@Column(name = "fnr", nullable = false)
	private String fnr;

	@Column(name = "aktor_id", nullable = false)
	private String aktorId;

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	@Column(name = "bestilling_tidspunkt", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime bestillingTidspunkt;

	@Column(name = "revarsling_intervall")
	private Integer revarslingIntervall;

	@Column(name = "antall_revarslinger")
	private Integer antallRevarslinger;

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	@Column(name = "neste_varsling_tidspunkt", columnDefinition = "TIMESTAMP")
	private LocalDateTime nesteVarslingstidspunkt;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "varselbestilling")
	private Set<Varsel> varsels = new HashSet<>();

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "FLETTE_PARAMETER", joinColumns = @JoinColumn(name = "FK_VARSELBESTILLING_ID"))
	@MapKeyColumn(name = "KEY")
	@Column(name = "VALUE")
	private Map<String, String> fletteparametere = new HashMap<>();

	public Varselbestilling(Long id, Long versjon) {
		this.id = id;
		setVersion(versjon);
	}

	public Varselbestilling() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVarselbestillingId() {
		return varselbestillingId;
	}

	public void setVarselbestillingId(String varselbestillingId) {
		this.varselbestillingId = varselbestillingId;
	}

	public String getVarslingstype() {
		return varslingstype;
	}

	public void setVarslingstype(String varslingstype) {
		this.varslingstype = varslingstype;
	}

	public String getPreferertKanal() {
		return preferertKanal;
	}

	public void setPreferertKanal(String preferertKanal) {
		this.preferertKanal = preferertKanal;
	}

	public LocalDateTime getUtlopTidspunkt() {
		return utlopTidspunkt;
	}

	public void setUtlopTidspunkt(LocalDateTime utlopTidspunkt) {
		this.utlopTidspunkt = utlopTidspunkt;
	}

	public String getFnr() {
		return fnr;
	}

	public void setFnr(String fnr) {
		this.fnr = fnr;
	}

	public String getAktorId() {
		return aktorId;
	}

	public void setAktorId(String aktorId) {
		this.aktorId = aktorId;
	}

	public LocalDateTime getBestillingTidspunkt() {
		return bestillingTidspunkt;
	}

	public void setBestillingTidspunkt(LocalDateTime bestillingTidspunkt) {
		this.bestillingTidspunkt = bestillingTidspunkt;
	}

	public Integer getRevarslingIntervall() {
		return revarslingIntervall;
	}

	public void setRevarslingIntervall(Integer revarslingIntervall) {
		this.revarslingIntervall = revarslingIntervall;
	}

	public Integer getAntallRevarslinger() {
		return antallRevarslinger;
	}

	public void setAntallRevarslinger(Integer antallRevarslinger) {
		this.antallRevarslinger = antallRevarslinger;
	}

	public LocalDateTime getNesteVarslingstidspunkt() {
		return nesteVarslingstidspunkt;
	}

	public void setNesteVarslingstidspunkt(LocalDateTime nesteVarslingstidspunkt) {
		this.nesteVarslingstidspunkt = nesteVarslingstidspunkt;
	}

	public Set<Varsel> getVarsels() {
		return varsels;
	}

	public void addVarsel(Varsel varsel) {
		if (varsel != null) {
			varsels.add(varsel);
			varsel.setVarselbestilling(this);
		}
	}

	public Map<String, String> getFletteParametere() {
		return fletteparametere;
	}

	public void addFletteParameter(String key, String value) {
		fletteparametere.put(key, value);
	}
}
