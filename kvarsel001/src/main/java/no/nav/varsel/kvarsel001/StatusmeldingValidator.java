package no.nav.varsel.kvarsel001;

import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.DoknotifikasjonStatus;
import no.nav.varsel.exception.functional.StatusmeldingValidationException;

import java.util.List;


@Slf4j
public class StatusmeldingValidator {

	private static final List<String> FEILET_ELLER_FERDIGSTILT = List.of("FEILET", "FERDIGSTILT");
	public static final String GYLDIG_BESTILLERID = "tms-ekstern-varsling";

	public static boolean validerStatusmelding(DoknotifikasjonStatus doknotifikasjonStatus) {

		return validerDistribusjonId(doknotifikasjonStatus) &&
				validerBestillingsId(doknotifikasjonStatus) &&
				validerBestillerId(doknotifikasjonStatus) &&
				validerStatus(doknotifikasjonStatus);
	}

	private static boolean validerDistribusjonId(DoknotifikasjonStatus doknotifikasjonStatus) {
		return doknotifikasjonStatus.getDistribusjonId() == null;
	}

	private static boolean validerStatus(DoknotifikasjonStatus doknotifikasjonStatus) {
		return FEILET_ELLER_FERDIGSTILT.contains(doknotifikasjonStatus.getStatus());
	}

	private static boolean validerBestillingsId(DoknotifikasjonStatus doknotifikasjonStatus) {
		if (doknotifikasjonStatus.getBestillingsId() == null) {
			throw new StatusmeldingValidationException("Statusmelding har bestillingsId=null. Avslutter behandling.");
		}
		return true;
	}

	private static boolean validerBestillerId(DoknotifikasjonStatus doknotifikasjonStatus) {
		return doknotifikasjonStatus.getBestillerId().equals(GYLDIG_BESTILLERID);
	}
}
