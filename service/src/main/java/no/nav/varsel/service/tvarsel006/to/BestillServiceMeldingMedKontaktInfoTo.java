package no.nav.varsel.service.tvarsel006.to;

import static no.nav.varsel.service.support.ValueValidator.hasText;

import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.service.to.BestillVarselCommonTo;

/**
 * To for BestillServiceMeldingMedKontaktInfo (TVARSEL006)
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class BestillServiceMeldingMedKontaktInfoTo extends BestillVarselCommonTo {

	private String orgNr;
	private String epost;
	private String mobiltelefonnummer;

	public String getOrgNr() {
		return orgNr;
	}

	public void setOrgNr(String orgNr) {
		this.orgNr = orgNr;
	}

	public String getEpost() {
		return epost;
	}

	public void setEpost(String epost) {
		this.epost = epost;
	}

	public String getMobiltelefonnummer() {
		return mobiltelefonnummer;
	}

	public void setMobiltelefonnummer(String mobiltelefonnummer) {
		this.mobiltelefonnummer = mobiltelefonnummer;
	}

	public void validateTvarsel006Input() {
		try {
			validate();
			hasText(orgNr, "organisasjonsnummer");
			hasText(epost == null ? mobiltelefonnummer : epost, "kontaktinformasjon");
		} catch (Exception e) {
			throw new NoJmsBackoutException("Validation failed for input, " + e.getMessage(), e);
		}
	}
}
