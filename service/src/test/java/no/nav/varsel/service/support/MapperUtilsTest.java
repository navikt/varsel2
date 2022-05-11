package no.nav.varsel.service.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselutsending;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MapperUtilsTest {

	static Varselutsending VARSELUTSENDING_SMS = createVarselutsending(SMS);
	static Varselutsending VARSELUTSENDING_EPOST = createVarselutsending(EPOST);
	static Varselutsending VARSELUTSENDING_DITT_NAV = createVarselutsending(DITT_NAV);

	@ParameterizedTest
	@MethodSource("titler")
	void mapTittel(List<Varselutsending> varselutsendingList, String tittel) {
		var resultat = MapperUtils.mapTittel(varselutsendingList);

		assertEquals(tittel, resultat);
	}

	private static Stream<Arguments> titler() {
		return Stream.of(
				arguments(List.of(VARSELUTSENDING_SMS, VARSELUTSENDING_EPOST), "Epost-tittel"),
				arguments(List.of(VARSELUTSENDING_SMS), "SMS fra NAV"),
				arguments(List.of(VARSELUTSENDING_EPOST), "Epost-tittel")
		);
	}


	@ParameterizedTest
	@MethodSource("tekster")
	void mapTekst(List<Varselutsending> varselutsendingList, KanalCode kanalCode, String tekst) {
		var resultat = MapperUtils.mapTekst(varselutsendingList, kanalCode);

		assertEquals(tekst, resultat);
	}

	private static Stream<Arguments> tekster() {
		return Stream.of(
				arguments(List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_SMS), EPOST, "Tekst i epost-varsel"),
				arguments(List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_SMS), SMS, "Tekst i sms-varsel"),
				arguments(List.of(VARSELUTSENDING_SMS), EPOST, "EPOST fra NAV"),
				arguments(List.of(VARSELUTSENDING_EPOST), SMS, "SMS fra NAV")
		);
	}


	@ParameterizedTest
	@MethodSource("kanaler")
	void mapKanaler(List<Varselutsending> varselutsendingList, List<PrefererteKanal> prefererteKanalList) {
		var resultat = MapperUtils.mapKanaler(varselutsendingList);

		assertEquals(prefererteKanalList, resultat);
	}

	private static Stream<Arguments> kanaler() {
		return Stream.of(
				arguments(List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_SMS, VARSELUTSENDING_DITT_NAV), List.of(PrefererteKanal.EPOST, PrefererteKanal.SMS)),
				arguments(List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_SMS), List.of(PrefererteKanal.EPOST, PrefererteKanal.SMS)),
				arguments(List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_DITT_NAV), List.of(PrefererteKanal.EPOST)),
				arguments(List.of(VARSELUTSENDING_SMS, VARSELUTSENDING_DITT_NAV), List.of(PrefererteKanal.SMS)),
				arguments(List.of(VARSELUTSENDING_EPOST), List.of(PrefererteKanal.EPOST)),
				arguments(List.of(VARSELUTSENDING_SMS), List.of(PrefererteKanal.SMS)),
				arguments(List.of(VARSELUTSENDING_DITT_NAV), Collections.emptyList())
		);
	}
}