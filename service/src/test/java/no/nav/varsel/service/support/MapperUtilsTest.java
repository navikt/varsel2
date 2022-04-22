package no.nav.varsel.service.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.MapperUtils.mapKanalToSingletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class MapperUtilsTest {

	@ParameterizedTest
	@EnumSource(value = KanalCode.class, names = {"DITT_NAV"}, mode = EXCLUDE)
	void shouldMapKanalToSingletonList(KanalCode kanalCode) {
		var preferertKanalListe = mapKanalToSingletonList(kanalCode);

		assertEquals(singletonList(PrefererteKanal.valueOf(kanalCode.name())), preferertKanalListe);
	}

	@ParameterizedTest
	@EnumSource(value = KanalCode.class, names = {"DITT_NAV"})
	void shouldNotMapKanalDittNavToSingletonList(KanalCode kanalCode) {
		var preferertKanalListe = mapKanalToSingletonList(kanalCode);

		assertTrue(preferertKanalListe.isEmpty());
	}

	@ParameterizedTest
	@MethodSource("titler")
	void mapTittel(KanalCode kanalCode, String tittel) {
		Set<VarselMalTo> maler = getMaler();

		var varselinfo = VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo()
				.maler(maler)
				.build();

		var resultat = MapperUtils.mapTittel(kanalCode, varselinfo);

		assertEquals(tittel, resultat);
	}

	private static Stream<Arguments> titler() {
		return Stream.of(
				arguments(EPOST, "Epost-tittel"),
				arguments(SMS, "SMS fra NAV")
		);
	}

	private Set<VarselMalTo> getMaler() {
		return Stream.of(
						VarselMalTo.VarselMalToBuilder.aVarselMalTo()
								.kanal(EPOST)
								.tittel("Epost-tittel")
								.build(),
						VarselMalTo.VarselMalToBuilder.aVarselMalTo()
								.kanal(DITT_NAV)
								.tittel("Ditt Nav-tittel")
								.build())
				.collect(Collectors.toSet());
	}
}