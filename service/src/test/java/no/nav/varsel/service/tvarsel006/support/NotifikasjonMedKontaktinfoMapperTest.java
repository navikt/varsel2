package no.nav.varsel.service.tvarsel006.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.APPNAVN;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.EPOSTADRESSE;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.FNR;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.MOBILNUMMER;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.VARSELBESTILLINGS_ID;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.VARSELTEKST_EPOST;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.VARSELTITTEL_EPOST;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.VARSELTITTEL_SMS;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createBestillVarselTo;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselbestilling;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselutsending;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotifikasjonMedKontaktinfoMapperTest {

	private final NotifikasjonMedKontaktinfoMapper notifikasjonMedkontaktInfoMapper;

	public NotifikasjonMedKontaktinfoMapperTest() {
		this.notifikasjonMedkontaktInfoMapper = new NotifikasjonMedKontaktinfoMapper(APPNAVN);
	}

	@Test
	public void shouldMapNotifikasjonMedKontaktinfoEpost() {
		var bestillVarselTo = createBestillVarselTo();
		var varselbestilling = createVarselbestilling();
		var varselutsendingList = List.of(createVarselutsending(EPOST));

		var notifikasjonMedKontaktInfo = notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(
				varselutsendingList,
				varselbestilling,
				bestillVarselTo
		);

		assertEquals(VARSELBESTILLINGS_ID, notifikasjonMedKontaktInfo.getBestillingsId());
		assertEquals(FNR, notifikasjonMedKontaktInfo.getFodselsnummer());
		assertEquals(MOBILNUMMER, notifikasjonMedKontaktInfo.getMobiltelefonnummer());
		assertEquals(EPOSTADRESSE, notifikasjonMedKontaktInfo.getEpostadresse());
		assertEquals(VARSELTITTEL_EPOST, notifikasjonMedKontaktInfo.getTittel());
		assertEquals(VARSELTEKST_EPOST, notifikasjonMedKontaktInfo.getEpostTekst());
		assertEquals(String.format("%s fra NAV", SMS.name()), notifikasjonMedKontaktInfo.getSmsTekst());
		assertEquals(List.of(PrefererteKanal.EPOST), notifikasjonMedKontaktInfo.getPrefererteKanaler());
	}

	@Test
	public void shouldMapNotifikasjonMedKontaktinfoSMS() {
		var bestillVarselTo = createBestillVarselTo();
		var varselbestilling = createVarselbestilling();
		var varselutsendingList = List.of(createVarselutsending(SMS));

		var notifikasjonMedKontaktInfo = notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(
				varselutsendingList,
				varselbestilling,
				bestillVarselTo
		);

		assertEquals(VARSELTITTEL_SMS, notifikasjonMedKontaktInfo.getTittel());
		assertEquals(String.format("%s fra NAV", EPOST.name()), notifikasjonMedKontaktInfo.getEpostTekst());
		assertEquals(List.of(PrefererteKanal.SMS), notifikasjonMedKontaktInfo.getPrefererteKanaler());
	}
}
