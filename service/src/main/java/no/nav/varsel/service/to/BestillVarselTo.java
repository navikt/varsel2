package no.nav.varsel.service.to;


import lombok.ToString;
import no.nav.varsel.domain.exception.NoJmsBackoutException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static no.nav.varsel.service.support.ValueValidator.hasText;
import static no.nav.varsel.service.support.ValueValidator.notNull;

@ToString
public class BestillVarselTo extends AktoerBestillingTo {

	public static final String TESTVARSEL = "Testvarsel";
	private static final String VALIDATION_FAILED_FOR_INPUT = "Validation failed for input, ";
	protected String varseltypeId;
	@ToString.Exclude
	private Map<String, String> parameters = new HashMap<>();
	private LocalDateTime utloepstidspunkt;
	private String varselBestillingId;
	private LocalDateTime utsendelsesTidspunkt;
	private Boolean revarsling;
	private Boolean testvarsel = false;
	@ToString.Exclude
	private String orgNr;
	@ToString.Exclude
	private String epost;
	@ToString.Exclude
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

	public String getVarselBestillingId() {
		return varselBestillingId;
	}

	public void setVarselBestillingId(String varselBestillingId) {
		this.varselBestillingId = varselBestillingId;
	}

	private void validate() {
		assertHasOneIdent();
		hasText(varseltypeId, "varseltypeId");
		parameters.forEach((key, val) -> {
			hasText(key, "parameter.key");
			hasText(val, "parameter.value");
		});
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

	public boolean isTestvarsel() {
		return testvarsel;
	}

	public void setTestvarsel(boolean testvarsel) {
		this.testvarsel = testvarsel;
	}

	public void validateTvarsel001Input() {
		try {
			validate();
		} catch (Exception e) {
			throw new NoJmsBackoutException(VALIDATION_FAILED_FOR_INPUT + e.getMessage(), e);
		}
	}

	public void validateTvarsel003Input() {
		try {
			validate();
			hasText(varselBestillingId, "varselBestillingId");
			notNull(revarsling, "revarsling");
		} catch (Exception e) {
			throw new NoJmsBackoutException(VALIDATION_FAILED_FOR_INPUT + e.getMessage(), e);
		}
	}

	public void validateTvarsel006Input() {
		try {
			validate();
			hasText(orgNr, "organisasjonsnummer");
			hasText(epost == null ? mobiltelefonnummer : epost, "kontaktinformasjon");
		} catch (Exception e) {
			throw new NoJmsBackoutException(VALIDATION_FAILED_FOR_INPUT + e.getMessage(), e);
		}
	}
}
