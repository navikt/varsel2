package no.nav.varsel.tvarsel001.service.service.to;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;

import static no.nav.varsel.domain.to.AktoerTo.newAktoerId;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.tvarsel001.service.service.support.ValueValidator.hasText;

public class AktoerBestillingTo {
	protected String personIdent;
	protected String aktoerId;

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

	protected void assertHasOneIdent() {
		hasText(personIdent == null ? aktoerId : personIdent, "mottaker");
	}

	public AktoerTo createAktoerTo() {
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
