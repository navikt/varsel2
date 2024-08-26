package no.nav.varsel.consumer.dokmet;

import no.nav.dokmet.api.tkat021.VarselInfoTo;
import no.nav.dokmet.api.tkat021.VarselMalTo;
import no.nav.varsel.domain.code.KanalCode;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public class VarselinfoMapper {

	public static Varselinfo mapToVarselinfo(VarselInfoTo varselinfoTo) {
		notNull(varselinfoTo, "varselinfoTo is null");

		Set<VarselMalTo> varselmaler = varselinfoTo.getVarselmals();

		return Varselinfo.builder()
				.varseltypeId(varselinfoTo.getVarseltypeId())
				.varselNavn(varselinfoTo.getVarselNavn())
				.varselForDistKanal(varselinfoTo.getVarselForDistribusjonKanal())
				.varselKategori(varselinfoTo.getVarselKategori())
				.inaktiv(varselinfoTo.getInaktiv())
				.revarslingIntervall(varselinfoTo.getRevarslingIntervall())
				.antallRevarsling(varselinfoTo.getAntallRevarslinger())
				.varselUrl(varselinfoTo.getVarselURL())
				.preferertKanal(mapKanaler(varselinfoTo.getPreferertKanal()))
				.maler(varselmaler == null ? null : varselmaler.stream().map(VarselinfoMapper::mapMal).collect(toSet()))
				.build();
	}

	private static Varselmal mapMal(VarselMalTo varselMal) {
		return new Varselmal(
				mapKanal(varselMal.getKanal()),
				varselMal.getVarselTittel(),
				varselMal.getFoerstegangsvarselTekst(),
				varselMal.getRevarslingTekst()
		);
	}

	private static Set<KanalCode> mapKanaler(Set<String> preferertKanal) {
		notNull(preferertKanal, "preferertKanal is null");

		return preferertKanal.stream()
				.map(VarselinfoMapper::mapKanal)
				.collect(toSet());
	}

	private static KanalCode mapKanal(String kanal) {
		hasText(kanal, "kanal is empty");

		return KanalCode.valueOf(kanal);
	}

}