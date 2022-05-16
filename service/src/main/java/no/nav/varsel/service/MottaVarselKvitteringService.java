package no.nav.varsel.service;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.service.support.exception.functional.InvalidVarselStatusException;
import no.nav.varsel.service.support.exception.functional.VarselNotExistException;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringStatusTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

/**
 * Service for MottaVarselKvittering
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class MottaVarselKvitteringService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MottaVarselKvitteringService.class);
	static final int MAX_LENGTH_FEILMELDING = 1000;
	
	@Autowired
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
			throw new InvalidVarselStatusException(varsel.getVarselId(), varsel.getStatus().toString());
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
					". feilmelding=" + sensurerPersonligData(mottaVarselKvitteringTo.getFeilmelding()));
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

	static String sensurerPersonligData(String melding) {
		String fnrRegex = "(User:\\s)\\d{11}";
		String epostRegex = "(?i)(Address: [a-zæøåÆØÅ0-9!#$%&'*+/=?^_`{|}~-])(?:[a-zæøåÆØÅ0-9!#$%&'*+/=?^_`{|}~-]*(?:\\.[a-zæøåÆØÅ0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@([a-zæøåÆØÅ0-9!#$%&'*+/=?^_`{|}~-])(?:(?:[a-zæøåÆØÅ0-9](?:[a-zæøåÆØÅ0-9-]*[a-zæøåÆØÅ0-9])?\\.)*([a-zæøåÆØÅ0-9](?:[a-zæøåÆØÅ0-9-]*[a-zæøåÆØÅ0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-zæøåÆØÅ0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\]))";
		String tlfRegex = "(country code:\\s\\+\\d{4})(\\d+)";
		return melding.replaceAll(fnrRegex, "$1****") // Sensurer fnr
				.replaceAll(epostRegex, "$1***@$2***.$3") // Sensurer epostadresse
				.replaceAll(tlfRegex, "$1****"); // Sensurer telefonnr
	}
}
