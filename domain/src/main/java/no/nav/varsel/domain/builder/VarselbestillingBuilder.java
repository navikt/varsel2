package no.nav.varsel.domain.builder;

import no.nav.varsel.domain.auxiliary.Builder;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Builder for {@link Varselbestilling}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public final class VarselbestillingBuilder extends Builder<Varselbestilling> {

	private Long id;
	private String varselbestillingId;
	private String varseltypeId;
	private LocalDateTime utlopTidspunkt;
	private String fnr;
	private String aktorId;
	private LocalDateTime bestillingTidspunkt;
	private Integer revarslingIntervall;
	private Integer antallRevarslinger;
	private LocalDate nesteVarslingDato;
	private Set<Varsel> varsels = new HashSet<>();
	private Map<String, String> parameters = new HashMap<>();

	private VarselbestillingBuilder() {
	}

	public static VarselbestillingBuilder aVarselbestilling() {
		return new VarselbestillingBuilder();
	}

	public VarselbestillingBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public VarselbestillingBuilder varselbestillingId(String varselbestillingId) {
		this.varselbestillingId = varselbestillingId;
		return this;
	}

	public VarselbestillingBuilder varseltypeId(String varseltypeId) {
		this.varseltypeId = varseltypeId;
		return this;
	}

	public VarselbestillingBuilder utlopTidspunkt(LocalDateTime utlopTidspunkt) {
		this.utlopTidspunkt = utlopTidspunkt;
		return this;
	}

	public VarselbestillingBuilder fnr(String fnr) {
		this.fnr = fnr;
		return this;
	}

	public VarselbestillingBuilder aktorId(String aktorId) {
		this.aktorId = aktorId;
		return this;
	}

	public VarselbestillingBuilder bestillingTidspunkt(LocalDateTime bestillingTidspunkt) {
		this.bestillingTidspunkt = bestillingTidspunkt;
		return this;
	}

	public VarselbestillingBuilder revarslingIntervall(Integer revarslingIntervall) {
		this.revarslingIntervall = revarslingIntervall;
		return this;
	}

	public VarselbestillingBuilder antallRevarslinger(Integer antallRevarslinger) {
		this.antallRevarslinger = antallRevarslinger;
		return this;
	}

	public VarselbestillingBuilder nesteVarslingDato(LocalDate nesteVarslingDato) {
		this.nesteVarslingDato = nesteVarslingDato;
		return this;
	}

	public VarselbestillingBuilder varsel(Varsel... varsels) {
		this.varsels.addAll(Arrays.asList(varsels));
		return this;
	}

	public VarselbestillingBuilder varsel(Collection<Varsel> varsels) {
		this.varsels.addAll(varsels);
		return this;
	}

	public VarselbestillingBuilder parameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
		return this;
	}

	public VarselbestillingBuilder parameter(String key, String value) {
		parameters.put(key, value);
		return this;
	}

	public Varselbestilling build() {
		Varselbestilling varselbestilling = new Varselbestilling();
		varselbestilling.setId(id);
		varselbestilling.setVarselbestillingId(varselbestillingId);
		varselbestilling.setVarseltypeId(varseltypeId);
		varselbestilling.setUtlopTidspunkt(utlopTidspunkt);
		varselbestilling.setFnr(fnr);
		varselbestilling.setAktorId(aktorId);
		varselbestilling.setBestillingTidspunkt(bestillingTidspunkt);
		varselbestilling.setRevarslingIntervall(revarslingIntervall);
		varselbestilling.setAntallRevarslinger(antallRevarslinger);
		varselbestilling.setNesteVarslingDato(nesteVarslingDato);
		varsels.forEach(varselbestilling::addVarsel);
		parameters.forEach(varselbestilling::addFletteParameter);
		return varselbestilling;
	}
}
