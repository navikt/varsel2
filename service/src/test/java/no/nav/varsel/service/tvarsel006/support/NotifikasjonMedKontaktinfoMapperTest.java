package no.nav.varsel.service.tvarsel006.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.to.BestillVarselTo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELTEKST_EPOST;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELTITTEL_EPOST;
import static no.nav.varsel.service.support.ServicemeldingUtil.VARSELTITTEL_SMS;
import static no.nav.varsel.service.support.ServicemeldingUtil.createVarselutsending;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Disabled
public class NotifikasjonMedKontaktinfoMapperTest {

	private static final String MOBILNUMMER = "12345678";
	private static final String EPOST = "epost@post.no";
	private static final String FNR = "12345678910";
	private static final String BESTILLING_ID = "ABC-12345";
	private final NotifikasjonMedKontaktinfoMapper notifikasjonMedkontaktInfoMapper;

	public NotifikasjonMedKontaktinfoMapperTest() {
		this.notifikasjonMedkontaktInfoMapper = new NotifikasjonMedKontaktinfoMapper();
	}

	@Test
	public void shouldMapNotifikasjonMedKontaktinfoEpost() {
		var bestillVarselTo = createBestillVarselTo();
		var varselbestilling = createVarselBestilling();
		var varselutsendingList = List.of(createVarselutsending(KanalCode.EPOST));

		var notifikasjonMedKontaktInfo = notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(
				varselutsendingList,
				varselbestilling,
				bestillVarselTo
		);

		assertEquals(BESTILLING_ID, notifikasjonMedKontaktInfo.getBestillingsId());
		assertEquals(FNR, notifikasjonMedKontaktInfo.getFodselsnummer());
		assertEquals(MOBILNUMMER, notifikasjonMedKontaktInfo.getMobiltelefonnummer());
		assertEquals(EPOST, notifikasjonMedKontaktInfo.getEpostadresse());
		assertEquals(VARSELTITTEL_EPOST, notifikasjonMedKontaktInfo.getTittel());
		assertEquals(VARSELTEKST_EPOST, notifikasjonMedKontaktInfo.getEpostTekst());
		assertNull(notifikasjonMedKontaktInfo.getSmsTekst());
		assertEquals(List.of(PrefererteKanal.EPOST), notifikasjonMedKontaktInfo.getPrefererteKanaler());
	}

	@Test
	public void shouldMapNotifikasjonMedKontaktinfoSMS() {
		var bestillVarselTo = createBestillVarselTo();
		var varselbestilling = createVarselBestilling();
		var varselutsendingList = List.of(createVarselutsending(KanalCode.SMS));

		var notifikasjonMedKontaktInfo = notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(
				varselutsendingList,
				varselbestilling,
				bestillVarselTo
		);

		assertEquals(VARSELTITTEL_SMS, notifikasjonMedKontaktInfo.getTittel());
		assertEquals(List.of(PrefererteKanal.SMS), notifikasjonMedKontaktInfo.getPrefererteKanaler());
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

}
