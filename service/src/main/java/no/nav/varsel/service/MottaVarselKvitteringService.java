package no.nav.varsel.service;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.service.support.exception.InvalidVarselStatusException;
import no.nav.varsel.service.support.exception.VarselNotExistException;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringStatusTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Service for MottaVarselKvittering
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */

public class MottaVarselKvitteringService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MottaVarselKvitteringService.class);
	static final int MAX_LENGTH_FEILMELDING = 1000;
	
	@Inject
	private VarselRepo varselRepo;

	@Retryable(include = VarselNotExistException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000, multiplier = 3))
	public void behandleKvitteringsmelding(MottaVarselKvitteringTo mottaVarselKvitteringTo) {
		Varsel varsel = findVarsel(mottaVarselKvitteringTo);
		validateVarselStatus(varsel);
		updateVarsel(mottaVarselKvitteringTo, varsel);
	}

	private Varsel findVarsel(MottaVarselKvitteringTo mottaVarselKvitteringTo) {
		Varsel varsel = varselRepo.findByVarselId(mottaVarselKvitteringTo.getVarselId());
		if (varsel == null) {
			throw new VarselNotExistException(mottaVarselKvitteringTo.getVarselId());
		}
		return varsel;
	}

	private void validateVarselStatus(Varsel varsel) {
		if (!(varsel.getStatus() == StatusCode.SENDT || varsel.getStatus() == StatusCode.FERDIGBEHANDLET)) {
			throw new InvalidVarselStatusException(varsel);
		}
	}
	
	private void updateVarsel(MottaVarselKvitteringTo mottaVarselKvitteringTo, Varsel varsel) {
		varsel.setKvitteringTidspunkt(LocalDateTime.now());
		if (mottaVarselKvitteringTo.getStatus() == MottaVarselKvitteringStatusTo.OK) {
			LOG.info("Varsel med status=" + varsel.getStatus() + " og varselId=" + mottaVarselKvitteringTo.getVarselId() + " er oppdatert til status=FERDIGBEHANDLET");
			varsel.setStatus(StatusCode.FERDIGBEHANDLET);
			varsel.setDistribusjonTidspunkt(mottaVarselKvitteringTo.getUtsendingstidspunkt());
		} else if (mottaVarselKvitteringTo.getStatus() == MottaVarselKvitteringStatusTo.ERROR
				|| mottaVarselKvitteringTo.getStatus() == MottaVarselKvitteringStatusTo.EXPIRED) {
			LOG.warn("Error/Expired kvittering for varsel with varselId=" + mottaVarselKvitteringTo.getVarselId() +
					". feilmelding=" + sesurerSensitivData(mottaVarselKvitteringTo.getFeilmelding()));
			varsel.setStatus(StatusCode.FEILET);
			varsel.setFeilbeskrivelse(StringUtils.abbreviate(mottaVarselKvitteringTo.getFeilmelding(),
					MAX_LENGTH_FEILMELDING));
			if (StringUtils.length(mottaVarselKvitteringTo.getFeilmelding()) > MAX_LENGTH_FEILMELDING) {
				LOG.warn("Kvittering with varselId=" + mottaVarselKvitteringTo.getVarselId() + " , " +
						"has Feilmelding with length longer than 1000. " +
						"feilmelding=" + mottaVarselKvitteringTo.getFeilmelding());
			}
		}
	}

	private String sesurerSensitivData(String melding) {
        return melding.replaceAll("(User:\\s)\\d{11}", "$1****") // Sensurer fnr
                .replaceAll("(Address:\\s[\\wæøå])[\\wæøå]+(@[\\wæøå])[\\wæøå]+(\\.[\\wæøå])+", "$1***$2***$3") // Sensurer epostadresse
                .replaceAll("(country code:\\s\\+\\d{4})(\\d+)", "$1****"); // Sensurer telefonnr
	}
}
