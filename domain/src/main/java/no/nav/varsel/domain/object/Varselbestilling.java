package no.nav.varsel.domain.object;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.auxiliary.AbstractDomainObject;
import no.nav.varsel.domain.code.KanalCode;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "varselbest_prefkanal", joinColumns = @JoinColumn(name = "fk_varselbestilling_id"))
	@Column(name = "k_kanal")
	@Enumerated(EnumType.STRING)
	private Set<KanalCode> preferertKanal = Sets.newHashSet();

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
	@Column(name = "neste_varsling_tidspunkt", columnDefinition = "DATE")
	private LocalDate nesteVarslingstidspunkt;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "varselbestilling")
	private Set<Varsel> varsels = new HashSet<>();

	@ElementCollection(fetch = FetchType.EAGER)
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

	public String getVarslingstype() {
		return varslingstype;
	}

	public void setVarslingstype(String varslingstype) {
		this.varslingstype = varslingstype;
	}

	public Set<KanalCode> getPreferertKanal() {
		return preferertKanal;
	}

	public void setPreferertKanal(Set<KanalCode> preferertKanal) {
		this.preferertKanal = preferertKanal;
	}

	public void addPreferertKanal(KanalCode preferertKanal) {
		this.preferertKanal.add(preferertKanal);
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

	public LocalDate getNesteVarslingstidspunkt() {
		return nesteVarslingstidspunkt;
	}

	public void setNesteVarslingstidspunkt(LocalDate nesteVarslingstidspunkt) {
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
