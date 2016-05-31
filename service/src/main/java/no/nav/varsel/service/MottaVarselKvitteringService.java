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
		if (varsel.getStatus() != StatusCode.SENDT) {
			throw new InvalidVarselStatusException(varsel);
		}
	}

	private void updateVarsel(MottaVarselKvitteringTo mottaVarselKvitteringTo, Varsel varsel) {
		varsel.setKvitteringTidspunkt(LocalDateTime.now());
		if (mottaVarselKvitteringTo.getStatus() == MottaVarselKvitteringStatusTo.PLUKKET) {
			varsel.setStatus(StatusCode.FERDIGBEHANDLET);
			varsel.setDistribusjonTidspunkt(mottaVarselKvitteringTo.getUtsendingstidspunkt());
		} else if (mottaVarselKvitteringTo.getStatus() == MottaVarselKvitteringStatusTo.FEILET) {
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
}
