package no.nav.varsel.service.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.MapperUtils.mapKanalToSingletonList;
import static no.nav.varsel.service.support.ServicemeldingUtil.createVarselutsending;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class MapperUtilsTest {

	static Varselutsending VARSELUTSENDING_SMS = createVarselutsending(SMS);
	static Varselutsending VARSELUTSENDING_EPOST = createVarselutsending(EPOST);

	@ParameterizedTest
	@EnumSource(value = KanalCode.class, names = {"DITT_NAV"}, mode = EXCLUDE)
	void shouldMapKanalToSingletonList(KanalCode kanalCode) {
		var preferertKanalListe = mapKanalToSingletonList(kanalCode);

		assertEquals(singletonList(PrefererteKanal.valueOf(kanalCode.name())), preferertKanalListe);
	}

	@Test
	void shouldThrowExceptionOnMapKanalDittNav() {
		assertThrows(IllegalArgumentException.class, () -> mapKanalToSingletonList(DITT_NAV));
	}

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

}