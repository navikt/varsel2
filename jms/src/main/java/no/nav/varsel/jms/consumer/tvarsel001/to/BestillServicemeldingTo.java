package no.nav.varsel.jms.consumer.tvarsel001.to;

import static no.nav.varsel.ValueValidator.hasText;

import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * To for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingTo {

	private String mottaker;
	private String varslingstype;
	private Map<String, String> parameters = new HashMap<>();
	private LocalDateTime utloepstidspunkt;

	public String getMottaker() {
		return mottaker;
	}

	public void setMottaker(String mottaker) {
		this.mottaker = mottaker;
	}

	public String getVarslingstype() {
		return varslingstype;
	}

	public void setVarslingstype(String varslingstype) {
		this.varslingstype = varslingstype;
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

	public void validate() {
		hasText(mottaker, "mottaker");
		hasText(varslingstype, "varslingstype");
		parameters.forEach((key, val) -> {
			hasText(key, "parameter.key");
			hasText(val, "parameter.value");
		});
	}
}
