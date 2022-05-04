package no.nav.varsel.service.tvarsel001.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.service.support.ServicemeldingUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.ServicemeldingUtil.APPNAVN;
import static no.nav.varsel.service.support.ServicemeldingUtil.SIKKERHETSNIVAA;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELTEKST_EPOST;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELTEKST_SMS;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELTITTEL_EPOST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotifikasjonMapperTest {

	private final NotifikasjonMapper notifikasjonMapper = new NotifikasjonMapper(APPNAVN);

	@Test
	void shouldMapNotifikasjon() {
		var varselbestilling = ServicemeldingUtil.createVarselbestilling();
		var varselutsendingList = ServicemeldingUtil.createVarselutsendingForKanaler(List.of(EPOST, SMS));

		var notifikasjon = notifikasjonMapper.mapNotifikasjon(varselutsendingList, varselbestilling);


		assertEquals(varselbestilling.getVarselbestillingId(), notifikasjon.getBestillingsId());
		assertEquals(APPNAVN, notifikasjon.getBestillerId());
		assertEquals(varselbestilling.getFnr(), notifikasjon.getFodselsnummer());
		assertEquals(VARSELTITTEL_EPOST, notifikasjon.getTittel());
		assertEquals(VARSELTEKST_EPOST, notifikasjon.getEpostTekst());
		assertEquals(VARSELTEKST_SMS, notifikasjon.getSmsTekst());
		assertTrue(notifikasjon.getPrefererteKanaler().containsAll(List.of(PrefererteKanal.EPOST, PrefererteKanal.SMS)));
		assertEquals(SIKKERHETSNIVAA, notifikasjon.getSikkerhetsnivaa());
	}
}