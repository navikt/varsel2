package no.nav.varsel.service.tvarsel001.support;

import no.nav.varsel.service.support.ServicemeldingTestUtils;
import no.nav.varsel.service.support.exception.functional.ServicemeldingMappingException;
import org.junit.jupiter.api.Test;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.APPNAVN;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.NAMESPACE;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.SIKKERHETSNIVAA;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.VARSELBESTILLINGS_ID;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.VARSELTEKST_DITT_NAV;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.VARSEL_URL;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselbestilling;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselutsending;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselutsendingMedUgyldigUrl;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselutsendingUtenUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrukernotifikasjonMapperTest {

	private final BrukernotifikasjonMapper brukernotifikasjonMapper = new BrukernotifikasjonMapper(APPNAVN);

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
		var varselutsending = createVarselutsending(DITT_NAV);

		var beskjed = brukernotifikasjonMapper.mapBeskjed(varselutsending);

		assertEquals(VARSELTEKST_DITT_NAV, beskjed.getTekst());
		assertEquals(VARSEL_URL, beskjed.getLink());
		assertEquals(SIKKERHETSNIVAA, beskjed.getSikkerhetsnivaa());
		assertFalse(beskjed.getEksternVarsling());
	}

	@Test
	public void shouldMapBeskjedUtenUrl() {
		var varselutsending = createVarselutsendingUtenUrl(DITT_NAV);

		var beskjed = brukernotifikasjonMapper.mapBeskjed(varselutsending);

		assertEquals(VARSELTEKST_DITT_NAV, beskjed.getTekst());
		assertEquals("",beskjed.getLink());
		assertEquals(SIKKERHETSNIVAA, beskjed.getSikkerhetsnivaa());
		assertFalse(beskjed.getEksternVarsling());
	}

	@Test
	public void shouldFailOnInvalidVarselUrl() {
		var varselutsending = createVarselutsendingMedUgyldigUrl(DITT_NAV);

		Exception e = assertThrows(ServicemeldingMappingException.class, () -> brukernotifikasjonMapper.mapBeskjed(varselutsending));
		assertTrue(e.getMessage().contains("Ugyldig URL i varselbestilling"));
	}
}
