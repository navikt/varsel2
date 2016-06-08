package no.nav.varsel.service.to;

import static no.nav.varsel.domain.to.AktoerTo.newAktoerId;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.service.support.ValueValidator.hasText;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;

/**
 * Common object for Bestilling With Aktoer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerBestillingTo {
	protected String personIdent;
	protected String aktoerId;
	protected String personidentType;

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

	public String getPersonidentType() {
		return personidentType;
	}

	public void setPersonidentType(String personidentType) {
		this.personidentType = personidentType;
	}

	protected void assertHasOneIdent() {
		hasText(personIdent == null ? aktoerId : personIdent, "mottaker");
	}

	protected void assertOptionalPersonIdentType() {
		hasText(personIdent != null ? personidentType : "na", "personidentType");
	}

	public AktoerTo craeteAktoerTo() {
		boolean isAktoerId = personIdent == null;
		if (isAktoerId) {
			return newAktoerId(aktoerId);
		}
		return newPersonIdent(personIdent);
	}

	public void setMottaker(AktoerTo aktoer) {
		if (aktoer == null) {
			return;
		}
		if (aktoer.getMottakerType() == MottakerType.AKTOER) {
			setAktoerId(aktoer.getIdent());
		} else {
			setPersonIdent(aktoer.getIdent());
		}
	}

}
