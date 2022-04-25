package no.nav.varsel.service.tvarsel006.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.varsel.domain.code.KanalCode.SMS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotifikasjonMedKontaktinfoMapperTest {

	private static final String MOBILNUMMER = "12345678";
	private static final String EPOST = "epost@post.no";
	private static final String FNR = "12345678910";
	private static final String BESTILLING_ID = "ABC-12345";
	private static final String VARSEL_TITTEL = "Tittel";
	private static final String VARSEL_TEKST = "Varsel tekst";
	private static final String VARSEL_TITTEL_SMS = "SMS fra NAV";
	private final NotifikasjonMedKontaktinfoMapper notifikasjonMedkontaktInfoMapper;

	public NotifikasjonMedKontaktinfoMapperTest() {
		this.notifikasjonMedkontaktInfoMapper = new NotifikasjonMedKontaktinfoMapper();
	}

	@Test
	public void shouldMapNotifikasjonMedKontaktinfoEpost() {
		var bestillVarselTo = createBestillVarselTo();
		var varselbestilling = createVarselBestilling();
		var varselutsendingTo = createVarselutsendingTo(KanalCode.EPOST);
		var varselInfoTo = createVarselInfoTo();

		var notifikasjonMedKontaktInfo = notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(
				bestillVarselTo,
				varselbestilling,
				varselutsendingTo,
				varselInfoTo
		);

		assertEquals(BESTILLING_ID, notifikasjonMedKontaktInfo.getBestillingsId());
		assertEquals(FNR, notifikasjonMedKontaktInfo.getFodselsnummer());
		assertEquals(MOBILNUMMER, notifikasjonMedKontaktInfo.getMobiltelefonnummer());
		assertEquals(EPOST, notifikasjonMedKontaktInfo.getEpostadresse());
		assertEquals(VARSEL_TITTEL, notifikasjonMedKontaktInfo.getTittel());
		assertEquals(VARSEL_TEKST, notifikasjonMedKontaktInfo.getEpostTekst());
		assertEquals(VARSEL_TEKST, notifikasjonMedKontaktInfo.getSmsTekst());
		assertTrue(notifikasjonMedKontaktInfo.getPrefererteKanaler().contains(PrefererteKanal.EPOST));
	}

	@Test
	public void shouldMapNotifikasjonMedKontaktinfoSMS() {
		var bestillVarselTo = createBestillVarselTo();
		var varselbestilling = createVarselBestilling();
		var varselutsendingTo = createVarselutsendingTo(KanalCode.SMS);
		var varselInfoTo = createVarselInfoTo();

		var notifikasjonMedKontaktInfo = notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(
				bestillVarselTo,
				varselbestilling,
				varselutsendingTo,
				varselInfoTo
		);

		assertEquals(VARSEL_TITTEL_SMS, notifikasjonMedKontaktInfo.getTittel());
		assertTrue(notifikasjonMedKontaktInfo.getPrefererteKanaler().contains(PrefererteKanal.SMS));
	}


	private BestillVarselTo createBestillVarselTo() {
		var bestillVarselTo = new BestillVarselTo();
		bestillVarselTo.setMobiltelefonnummer(MOBILNUMMER);
		bestillVarselTo.setEpost(EPOST);
		return bestillVarselTo;
	}

	private Varselbestilling createVarselBestilling() {
		var varselbestilling = new Varselbestilling();
		varselbestilling.setVarselbestillingId(BESTILLING_ID);
		varselbestilling.setFnr(FNR);
		return varselbestilling;
	}

	private VarselutsendingTo createVarselutsendingTo(KanalCode kanalCode) {
		var varselutsendingTo = new VarselutsendingTo();
		varselutsendingTo.setVarselTittel(VARSEL_TITTEL);
		varselutsendingTo.setKanal(kanalCode);
		varselutsendingTo.setVarselTekst(VARSEL_TEKST);
		return varselutsendingTo;
	}

	private VarselInfoTo createVarselInfoTo() {
		var varselInfoTo = new VarselInfoTo();
		varselInfoTo.setMaler(Stream.of(createVarselMalTo(SMS), createVarselMalTo(KanalCode.EPOST))
				.collect(Collectors.toSet()));
		return varselInfoTo;
	}

	private VarselMalTo createVarselMalTo(KanalCode kanalCode) {
		var mal = new VarselMalTo();
		mal.setKanal(kanalCode);
		mal.setTittel(VARSEL_TITTEL);
		return mal;
	}
}
