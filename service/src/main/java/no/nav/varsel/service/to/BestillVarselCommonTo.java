package no.nav.varsel.service.to;

import static no.nav.varsel.service.support.ValueValidator.hasText;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Common object for Varselbestilling
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class BestillVarselCommonTo extends AktoerBestillingTo {

	protected String varseltypeId;
	protected Map<String, String> parameters = new HashMap<>();
	protected LocalDateTime utloepstidspunkt;

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

	protected void validate() {
		assertHasOneIdent();
		hasText(varseltypeId, "varseltypeId");
		parameters.forEach((key, val) -> {
			hasText(key, "parameter.key");
			hasText(val, "parameter.value");
		});
	}
}
