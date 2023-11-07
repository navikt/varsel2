package no.nav.varsel.consumer.dkif.to;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.varsel.domain.code.KanalCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KontaktregisterTo implements Serializable {

	private static final long serialVersionUID = 373780690208148963L;

	public static final int DATE_VALID_MONTHS = 18;

	private boolean reservasjon;
	private String epostadresse;
	private LocalDateTime epostSistOppdatert;
	private LocalDateTime epostSistVerifisert;
	private String mobiltelefonnummer;
	private LocalDateTime mobiltelefonSistOppdatert;
	private LocalDateTime mobiltelefonSistVerifisert;
	private String kontaktInfo;
	private Collection<KanalCode> kanaler;

	public boolean isEpostDateInvalid() {
		return isInvalid(getEpostSistOppdatert()) && isInvalid(getEpostSistVerifisert());
	}

	public boolean isMobilDateInvalid() {
		return isInvalid(getMobiltelefonSistOppdatert()) && isInvalid(getMobiltelefonSistVerifisert());
	}

	private boolean isInvalid(LocalDateTime dateTime) {
		return dateTime == null || LocalDate.now().minusMonths(DATE_VALID_MONTHS).isAfter(dateTime.toLocalDate());
	}

	public void cleanExpiredInfo() {
		if (isMobilDateInvalid()) {
			if(mobiltelefonnummer == null) {
				log.info("Sender ikke SMS. Mobiltelefonnummer er null. Sist oppdatert og sist verifisert må være maks 18 måneder siden. " +
						"mobiltelefonSistOppdatert={}, mobiltelefonSistVerifisert={}", mobiltelefonSistOppdatert, mobiltelefonSistVerifisert);
			} else {
				log.info("Sender ikke SMS. Mobiltelefonnummer er satt. Sist oppdatert og sist verifisert må være maks 18 måneder siden. " +
						"mobiltelefonSistOppdatert={}, mobiltelefonSistVerifisert={}", mobiltelefonSistOppdatert, mobiltelefonSistVerifisert);
			}
			setMobiltelefonnummer(null);
		}
		if (isEpostDateInvalid()) {
			if(epostadresse == null) {
				log.info("Sender ikke EPOST. Epostadresse er null. Sist oppdatert og sist verifisert må være maks 18 måneder siden. " +
						"epostSistOppdatert={}, epostSistVerifisert={}", epostSistOppdatert, epostSistVerifisert);
			} else {
				log.info("Sender ikke EPOST. Epostadresse er satt. Sist oppdatert og sist verifisert må være maks 18 måneder siden. " +
						"epostSistOppdatert={}, epostSistVerifisert={}", epostSistOppdatert, epostSistVerifisert);
			}
			setEpostadresse(null);
		}
	}
}
