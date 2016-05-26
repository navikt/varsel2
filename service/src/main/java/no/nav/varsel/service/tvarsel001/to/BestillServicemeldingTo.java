package no.nav.varsel.service.tvarsel001.to;

import static no.nav.varsel.service.support.ValueValidator.hasText;

import no.nav.varsel.domain.to.MottakerType;
import no.nav.varsel.wsconsumer.aktoer.to.AktoerTo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * To for {@link no.nav.varsel.service.ServicemeldingService}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingTo {

	// Input
	private String personIdent;
	private String aktoerId;
	private String varslingstype;
	private Map<String, String> parameters = new HashMap<>();
	private LocalDateTime utloepstidspunkt;

	public String getPersonIdent() {
		return personIdent;
	}

	public void setPersonIdent(String personIdent) {
		this.personIdent = personIdent;
	}

	public String getAktoerId() {
		return aktoerId;
	}

	public void setAktoerId(String aktoerId) {
		this.aktoerId = aktoerId;
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

	public void validateTvarsel001Input() {
		hasText(personIdent == null ? aktoerId : personIdent, "mottaker");
		hasText(varslingstype, "varslingstype");
		parameters.forEach((key, val) -> {
			hasText(key, "parameter.key");
			hasText(val, "parameter.value");
		});
	}

	public AktoerTo craeteAktoerTo() {
		AktoerTo aktoerTo = new AktoerTo();
		boolean isAktoerId = personIdent == null;
		aktoerTo.setIdent(isAktoerId ? aktoerId : personIdent);
		aktoerTo.setMottakerType(isAktoerId ? MottakerType.AKTOER : MottakerType.PERSON);
		return aktoerTo;
	}

	public void setMottaker(AktoerTo aktoer) {
		if (aktoer == null) {
			setAktoerId(null);
			setPersonIdent(null);
		}
		else if (aktoer.getMottakerType() == MottakerType.AKTOER) {
			setAktoerId(aktoer.getIdent());
		} else {
			setPersonIdent(aktoer.getIdent());
		}
	}
}
