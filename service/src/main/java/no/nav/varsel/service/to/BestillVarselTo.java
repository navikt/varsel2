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
public class BestillVarselTo extends BestillVarselCommonTo {

	public static final String TESTVARSEL = "Testvarsel";
	private String varselBestillingId;
	private LocalDateTime utsendelsesTidspunkt;
	private Boolean revarsling;

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
			throw new NoJmsBackoutException("Validation failed for input, " + e.getMessage(), e);
		}
	}

	public void validateTvarsel003Input() {
		try {
			validate();
			hasText(varselBestillingId, "varselBestillingId");
			notNull(revarsling, "revarsling");
		} catch (Exception e) {
			throw new NoJmsBackoutException("Validation failed for input, " + e.getMessage(), e);
		}
	}
}
