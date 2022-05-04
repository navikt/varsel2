package no.nav.varsel.service.tvarsel001.support;

import no.nav.varsel.service.support.ServicemeldingUtil;
import org.junit.jupiter.api.Test;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.service.support.ServicemeldingUtil.APPNAVN;
import static no.nav.varsel.service.support.ServicemeldingUtil.NAMESPACE;
import static no.nav.varsel.service.support.ServicemeldingUtil.SIKKERHETSNIVAA;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELBESTILLINGS_ID;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELTEKST_DITT_NAV;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSEL_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrukernotifikasjonMapperTest {

	private static final boolean EKSTERN_VARSLING = false;

	private final BrukernotifikasjonMapper brukernotifikasjonMapper = new BrukernotifikasjonMapper();

	@Test
	public void shouldMapNokkel() {
		var varselbestilling = ServicemeldingUtil.createVarselbestilling();

		var nokkel = brukernotifikasjonMapper.mapNokkel(varselbestilling);

		assertEquals(ServicemeldingUtil.FNR, nokkel.getFodselsnummer());
		assertEquals(VARSELBESTILLINGS_ID, nokkel.getGrupperingsId());
		assertEquals(VARSELBESTILLINGS_ID, nokkel.getGrupperingsId());
		assertEquals(NAMESPACE, nokkel.getNamespace());
		assertEquals(APPNAVN, nokkel.getAppnavn());
	}

	@Test
	public void shouldMapBeskjed() {
		var varselinfo = ServicemeldingUtil.createVarselInfoTo();
		var varselutsending = ServicemeldingUtil.createVarselutsending(DITT_NAV);

		var beskjed = brukernotifikasjonMapper.mapBeskjed(varselinfo, varselutsending);

		assertEquals(VARSELTEKST_DITT_NAV, beskjed.getTekst());
		assertEquals(VARSEL_URL, beskjed.getLink());
		assertEquals(SIKKERHETSNIVAA, beskjed.getSikkerhetsnivaa());
		assertEquals(EKSTERN_VARSLING, beskjed.getEksternVarsling());
	}

	@Test
	public void shouldFailOnInvalidVarselUrl() {
		var varselinfoMedUgyldigUrl = ServicemeldingUtil.createVarselInfoToWithInvalidUrl();
		var varselutsending = ServicemeldingUtil.createVarselutsending(DITT_NAV);

		Exception e = assertThrows(RuntimeException.class, () -> brukernotifikasjonMapper.mapBeskjed(varselinfoMedUgyldigUrl, varselutsending));
		assertTrue(e.getMessage().contains("Ugyldig URL i varselbestilling"));
	}
}
