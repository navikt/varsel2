package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.exception.functional.ServicemeldingMappingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.APPNAVN;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.NAMESPACE;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.SIKKERHETSNIVAA;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELBESTILLINGS_ID;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTEKST_DITT_NAV;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTEKST_EPOST;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTEKST_SMS;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTITTEL_EPOST;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSEL_URL;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createVarselbestilling;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createVarselutsending;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createVarselutsendingMedUgyldigUrl;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createVarselutsendingUtenUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrukernotifikasjonMapperTest {

	private static final Varselutsending DITT_NAV_VARSEL = createVarselutsending(DITT_NAV);
	private static final Varselutsending SMS_VARSEL = createVarselutsending(SMS);
	private static final Varselutsending EPOST_VARSEL = createVarselutsending(EPOST);

	private final BrukernotifikasjonMapper brukernotifikasjonMapper;

	public BrukernotifikasjonMapperTest() {
		VarselProperties varselProperties = new VarselProperties();
		varselProperties.setAppName(APPNAVN);
		this.brukernotifikasjonMapper = new BrukernotifikasjonMapper(varselProperties);
	}

	@Test
	public void shouldMapNokkel() {
		var varselbestilling = createVarselbestilling();

		var nokkel = brukernotifikasjonMapper.mapNokkel(varselbestilling);

		assertEquals(ServicemeldingTestUtils.FNR, nokkel.getFodselsnummer());
		assertEquals(VARSELBESTILLINGS_ID, nokkel.getGrupperingsId());
		assertEquals(VARSELBESTILLINGS_ID, nokkel.getGrupperingsId());
		assertEquals(NAMESPACE, nokkel.getNamespace());
		assertEquals(APPNAVN, nokkel.getAppnavn());
	}

	@Test
	public void shouldMapBeskjed() {
		var beskjed = brukernotifikasjonMapper.mapBeskjed(List.of(DITT_NAV_VARSEL, SMS_VARSEL, EPOST_VARSEL));

		assertEquals(VARSELTEKST_DITT_NAV, beskjed.getTekst());
		assertEquals(VARSEL_URL, beskjed.getLink());
		assertEquals(SIKKERHETSNIVAA, beskjed.getSikkerhetsnivaa());
		assertTrue(beskjed.getEksternVarsling());

		assertEquals(VARSELTEKST_SMS, beskjed.getSmsVarslingstekst());
		assertEquals(VARSELTEKST_EPOST, beskjed.getEpostVarslingstekst());
		assertEquals(VARSELTITTEL_EPOST, beskjed.getEpostVarslingstittel());
	}

	@Test
	void shouldMapBeskjedUtenSms() {
		var beskjed = brukernotifikasjonMapper.mapBeskjed(List.of(DITT_NAV_VARSEL, EPOST_VARSEL));

		assertNull(beskjed.getSmsVarslingstekst());
	}

	@Test
	void shouldMapBeskjedUtenEpost() {
		var beskjed = brukernotifikasjonMapper.mapBeskjed(List.of(DITT_NAV_VARSEL, SMS_VARSEL));

		assertNull(beskjed.getEpostVarslingstittel());
		assertNull(beskjed.getEpostVarslingstekst());
	}

	@Test
	void shouldMapBeskjedUtenSmsOgEpost() {
		var beskjed = brukernotifikasjonMapper.mapBeskjed(List.of(DITT_NAV_VARSEL));

		assertFalse(beskjed.getEksternVarsling());
		assertNull(beskjed.getSmsVarslingstekst());
		assertNull(beskjed.getEpostVarslingstittel());
		assertNull(beskjed.getEpostVarslingstekst());
	}

	@Test
	public void shouldMapBeskjedUtenUrl() {
		var varselutsending = createVarselutsendingUtenUrl(DITT_NAV);

		var beskjed = brukernotifikasjonMapper.mapBeskjed(List.of(varselutsending));

		assertEquals(VARSELTEKST_DITT_NAV, beskjed.getTekst());
		assertEquals("",beskjed.getLink());
		assertEquals(SIKKERHETSNIVAA, beskjed.getSikkerhetsnivaa());
		assertFalse(beskjed.getEksternVarsling());
	}

	@Test
	public void shouldFailOnInvalidVarselUrl() {
		var varselutsending = createVarselutsendingMedUgyldigUrl(DITT_NAV);

		Exception e = assertThrows(ServicemeldingMappingException.class, () -> brukernotifikasjonMapper.mapBeskjed(List.of(varselutsending)));
		assertTrue(e.getMessage().contains("Ugyldig URL i varselbestilling"));
	}
}
