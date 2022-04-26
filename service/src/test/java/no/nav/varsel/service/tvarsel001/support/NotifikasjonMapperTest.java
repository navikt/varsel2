package no.nav.varsel.service.tvarsel001.support;

import no.nav.varsel.service.support.ServicemeldingUtil;
import org.junit.jupiter.api.Test;

import static no.nav.varsel.service.support.ServicemeldingUtil.APPNAVN;
import static no.nav.varsel.service.support.ServicemeldingUtil.SIKKERHETSNIVAA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NotifikasjonMapperTest {

	private final NotifikasjonMapper notifikasjonMapper = new NotifikasjonMapper(APPNAVN);

	@Test
	void shouldMapNotifikasjon() {
		var varselbestilling = ServicemeldingUtil.createVarselbestilling();
		var varselutsendingTo = ServicemeldingUtil.createVarselutsending();
		var varselInfoTo = ServicemeldingUtil.createVarselInfoTo();

		var notifikasjon = notifikasjonMapper.mapNotifikasjon(varselbestilling, varselutsendingTo, varselInfoTo);

		assertEquals(varselbestilling.getVarselbestillingId(), notifikasjon.getBestillingsId());
		assertEquals(APPNAVN, notifikasjon.getBestillerId());
		assertEquals(varselbestilling.getFnr(), notifikasjon.getFodselsnummer());
		assertEquals(varselutsendingTo.getVarselTittel(), notifikasjon.getTittel());
		assertEquals(varselutsendingTo.getVarselTekst(), notifikasjon.getEpostTekst());
		assertEquals(varselutsendingTo.getVarselTekst(), notifikasjon.getSmsTekst());
		assertEquals(varselutsendingTo.getKanal().name(), notifikasjon.getPrefererteKanaler().get(0).name());
		assertEquals(SIKKERHETSNIVAA, notifikasjon.getSikkerhetsnivaa());
	}
}