package no.nav.varsel.domain.object;

import com.google.common.base.MoreObjects;
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
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
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
@Table(name = "VARSELBESTILLING", uniqueConstraints = @UniqueConstraint(columnNames = "varselbestilling_id"))
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

	@Column(name = "varseltype_id", nullable = false)
	private String varseltypeId;

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

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
	@Column(name = "neste_varsling_dato", columnDefinition = "DATE")
	private LocalDate nesteVarslingDato;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "varselbestilling")
	private Set<Varsel> varsels = new HashSet<>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "flette_parameter", joinColumns = @JoinColumn(name = "fk_varselbestilling_id"))
	@MapKeyColumn(name = "key")
	@Column(name = "value")
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

	public String getVarseltypeId() {
		return varseltypeId;
	}

	public void setVarseltypeId(String varseltypeId) {
		this.varseltypeId = varseltypeId;
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

	public LocalDate getNesteVarslingDato() {
		return nesteVarslingDato;
	}

	public void setNesteVarslingDato(LocalDate nesteVarslingDato) {
		this.nesteVarslingDato = nesteVarslingDato;
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

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("varselbestillingId", varselbestillingId)
				.add("varseltypeId", varseltypeId)
				.add("fnr", fnr)
				.add("aktorId", aktorId)
				.add("bestillingTidspunkt", bestillingTidspunkt)
				.add("nesteVarslingDato", nesteVarslingDato)
				.add("varsels", varsels)
				.toString();
	}
}
