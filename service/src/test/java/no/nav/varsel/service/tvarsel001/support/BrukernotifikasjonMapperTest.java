package no.nav.varsel.service.tvarsel001.support;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrukernotifikasjonMapperTest {

	private static final String VARSELBESTILLINGS_ID = UUID.randomUUID().toString();
	private static final String FNR = "12345678910";
	private static final String NAMESPACE = "teamdokumenthandtering";
	private static final String APPNAVN = "varsel";
	private static final Integer SIKKERHETSNIVAA = 3;
	private static final String VARSEL_URL = "https://www.varsel.com";
	private static final String UGYLDIG_VARSEL_URL = "httpp://www.invalidurl.com";
	private static final String VARSELTEKST = "Tekst i varselet";
	private static final boolean EKSTERN_VARSLING = false;

	private static final Instant TIDSPUNKT = LocalDateTime
			.of(2022, Month.APRIL, 21, 15, 56)
			.atZone(ZoneId.of("UTC"))
			.toInstant();

	private static final Clock FIXED_CLOCK = Clock.fixed(TIDSPUNKT, ZoneId.systemDefault());

	private final BrukernotifikasjonMapper brukernotifikasjonMapper = new BrukernotifikasjonMapper(FIXED_CLOCK);

	@Test
	public void shouldMapNokkel() {
		var varselbestilling = createVarselbestilling();

		var nokkel = brukernotifikasjonMapper.mapNokkel(varselbestilling);

		assertEquals(FNR, nokkel.getFodselsnummer());
		assertEquals(VARSELBESTILLINGS_ID, nokkel.getGrupperingsId());
		assertEquals(VARSELBESTILLINGS_ID, nokkel.getGrupperingsId());
		assertEquals(NAMESPACE, nokkel.getNamespace());
		assertEquals(APPNAVN, nokkel.getAppnavn());
	}

	@Test
	public void shouldMapBeskjed() {
		var varselinfo = createVarselInfoTo();
		var varselutsending = createVarselutsending();

		var beskjed = brukernotifikasjonMapper.mapBeskjed(varselinfo, varselutsending);

		assertEquals(TIDSPUNKT.toEpochMilli(), beskjed.getTidspunkt());
		assertEquals(VARSELTEKST, beskjed.getTekst());
		assertEquals(VARSEL_URL, beskjed.getLink());
		assertEquals(SIKKERHETSNIVAA, beskjed.getSikkerhetsnivaa());
		assertEquals(EKSTERN_VARSLING, beskjed.getEksternVarsling());
	}

	@Test
	public void shouldFailOnInvalidVarselUrl() {
		var varselinfoMedUgyldigUrl = createVarselInfoToWithInvalidUrl();
		var varselutsending = createVarselutsending();

		Exception e = assertThrows(RuntimeException.class, () -> brukernotifikasjonMapper.mapBeskjed(varselinfoMedUgyldigUrl, varselutsending));
		assertTrue(e.getMessage().contains("Ugyldig URL i varselbestilling"));
	}

	private Varselbestilling createVarselbestilling() {
		var varselbestilling = new Varselbestilling();

		varselbestilling.setVarselbestillingId(VARSELBESTILLINGS_ID);
		varselbestilling.setFnr(FNR);

		return varselbestilling;
	}

	private VarselInfoTo createVarselInfoTo() {
		return VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo()
				.varselUrl(VARSEL_URL)
				.build();
	}

	private VarselInfoTo createVarselInfoToWithInvalidUrl() {
		return VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo()
				.varselUrl(UGYLDIG_VARSEL_URL)
				.build();
	}

	private VarselutsendingTo createVarselutsending() {
		return VarselutsendingTo.VarselutsendingToBuilder.aVarselutsendingTo()
				.varselTekst(VARSELTEKST)
				.build();
	}
}
