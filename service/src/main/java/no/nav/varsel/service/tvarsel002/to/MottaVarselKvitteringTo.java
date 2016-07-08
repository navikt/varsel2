package no.nav.varsel.service.tvarsel002.to;

import static no.nav.varsel.service.support.ValueValidator.hasText;
import static no.nav.varsel.service.support.ValueValidator.notNull;

import java.time.LocalDateTime;

/**
 * To for {@link no.nav.varsel.service.MottaVarselKvitteringService}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class MottaVarselKvitteringTo {

	private String varselId;
	private String mottakerInformasjon;
	private LocalDateTime utsendingstidspunkt;
	private MottaVarselKvitteringStatusTo status;
	private String feilmelding;

	public void validateTo() {
		hasText(varselId, "varselId");
		notNull(utsendingstidspunkt, "utsendingstidspunkt");
		notNull(status, "status");
	}

	public String getVarselId() {
		return varselId;
	}

	public String getMottakerInformasjon() {
		return mottakerInformasjon;
	}

	public LocalDateTime getUtsendingstidspunkt() {
		return utsendingstidspunkt;
	}

	public MottaVarselKvitteringStatusTo getStatus() {
		return status;
	}

	public String getFeilmelding() {
		return feilmelding;
	}

	public void setVarselId(String varselId) {
		this.varselId = varselId;
	}

	public void setMottakerInformasjon(String mottakerInformasjon) {
		this.mottakerInformasjon = mottakerInformasjon;
	}

	public void setUtsendingstidspunkt(LocalDateTime utsendingstidspunkt) {
		this.utsendingstidspunkt = utsendingstidspunkt;
	}

	public void setStatus(MottaVarselKvitteringStatusTo status) {
		this.status = status;
	}

	public void setFeilmelding(String feilmelding) {
		this.feilmelding = feilmelding;
	}
}
