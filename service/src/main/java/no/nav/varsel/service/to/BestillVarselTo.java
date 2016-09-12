package no.nav.varsel.service.to;


import static no.nav.varsel.service.support.ValueValidator.hasText;
import static no.nav.varsel.service.support.ValueValidator.notNull;

import no.nav.varsel.domain.exception.NoJmsBackoutException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * To for BestillVarsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselTo extends AktoerBestillingTo {

	public static final String TESTVARSEL = "Testvarsel";
	private String varselBestillingId;
	private LocalDateTime utsendelsesTidspunkt;
	private Boolean revarsling;
	private String varseltypeId;
	private Map<String, String> parameters = new HashMap<>();
	private LocalDateTime utloepstidspunkt;
	private Boolean testvarsel = false;

	public String getVarselBestillingId() {
		return varselBestillingId;
	}

	public void setVarselBestillingId(String varselBestillingId) {
		this.varselBestillingId = varselBestillingId;
	}

	public LocalDateTime getUtsendelsesTidspunkt() {
		return utsendelsesTidspunkt;
	}

	public void setUtsendelsesTidspunkt(LocalDateTime utsendelsesTidspunkt) {
		this.utsendelsesTidspunkt = utsendelsesTidspunkt;
	}

	public Boolean isRevarsling() {
		return revarsling;
	}

	public void setRevarsling(Boolean revarsling) {
		this.revarsling = revarsling;
	}

	public String getVarseltypeId() {
		return varseltypeId;
	}

	public void setVarseltypeId(String varseltypeId) {
		this.varseltypeId = varseltypeId;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public LocalDateTime getUtloepstidspunkt() {
		return utloepstidspunkt;
	}

	public void setUtloepstidspunkt(LocalDateTime utloepstidspunkt) {
		this.utloepstidspunkt = utloepstidspunkt;
	}

	public boolean isTestvarsel() {
		return testvarsel;
	}

	public void setTestvarsel(boolean testvarsel) {
		this.testvarsel = testvarsel;
	}

	public void validateTvarsel001Input() {
		try {
			assertHasOneIdent();
			hasText(varseltypeId, "varseltypeId");
			parameters.forEach((key, val) -> {
				hasText(key, "parameter.key");
				hasText(val, "parameter.value");
			});
		} catch (Exception e) {
			throw new NoJmsBackoutException("Validation failed for input, " + e.getMessage(), e);
		}
	}

	public void validateTvarsel003Input() {
		try {
			hasText(varselBestillingId, "varselBestillingId");
			notNull(revarsling, "revarsling");
			assertHasOneIdent();
			hasText(varseltypeId, "varseltypeId");
			parameters.forEach((key, val) -> {
				hasText(key, "parameter.key");
				hasText(val, "parameter.value");
			});
		} catch (Exception e) {
			throw new NoJmsBackoutException("Validation failed for input, " + e.getMessage(), e);
		}
	}
}
