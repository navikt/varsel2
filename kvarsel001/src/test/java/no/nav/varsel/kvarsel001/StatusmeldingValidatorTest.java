package no.nav.varsel.kvarsel001;

import no.nav.varsel.service.support.exception.functional.StatusmeldingValidationException;
import org.junit.jupiter.api.Test;

import static no.nav.varsel.kvarsel001.StatusmeldingValidator.validerStatusmelding;
import static no.nav.varsel.kvarsel001.TestUtils.createDoknotifikasjonStatus;
import static org.junit.jupiter.api.Assertions.*;

class StatusmeldingValidatorTest {

	@Test
	void shouldNotValidateOnInvalidDistribusjonId() {
		var doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setDistribusjonId(1L);

		assertFalse(validerStatusmelding(doknotifikasjonStatus));
	}

	@Test
	void shouldNotValidateOnInvalidBestillingsId() {
		var doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setBestillingsId(null);

		var e = assertThrows(StatusmeldingValidationException.class, () -> validerStatusmelding(doknotifikasjonStatus));
		assertTrue(e.getMessage().contains("Statusmelding har bestillingsId=null"));
	}

	@Test
	void shouldNotValidateOnInvalidBestillerId() {
		var doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setBestillerId("ikke varsel");

		assertFalse(validerStatusmelding(doknotifikasjonStatus));
	}

	@Test
	void shouldNotValidateOnInvalidStatus() {
		var doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setStatus("OVERSENDT");

		assertFalse(validerStatusmelding(doknotifikasjonStatus));
	}


}