package no.nav.varsel.consumer.dokmet.support;

import no.nav.dokmet.api.tkat021.VarselInfoTo;
import no.nav.dokmet.api.tkat021.VarselMalTo;
import no.nav.varsel.consumer.dokmet.to.Varselinfo;
import no.nav.varsel.consumer.dokmet.to.Varselmal;
import no.nav.varsel.domain.code.KanalCode;
import org.springframework.util.Assert;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class VarselinfoMapper {

	public Varselinfo map(VarselInfoTo varselInfoTo) {
		Assert.notNull(varselInfoTo, "varselInfoTo is null");

		Set<VarselMalTo> varselmaler = varselInfoTo.getVarselmals();

		return Varselinfo.builder()
				.varseltypeId(varselInfoTo.getVarseltypeId())
				.varselNavn(varselInfoTo.getVarselNavn())
				.varselForDistKanal(varselInfoTo.getVarselForDistribusjonKanal())
				.varselKategori(varselInfoTo.getVarselKategori())
				.inaktiv(varselInfoTo.getInaktiv())
				.revarslingIntervall(varselInfoTo.getRevarslingIntervall())
				.antallRevarsling(varselInfoTo.getAntallRevarslinger())
				.varselUrl(varselInfoTo.getVarselURL())
				.preferertKanal(mapKanaler(varselInfoTo.getPreferertKanal()))
				.maler(varselmaler == null ? null : varselmaler.stream().map(this::mapMal).collect(toSet()))
				.build();
	}

	private Varselmal mapMal(VarselMalTo varselMal) {
		return Varselmal.builder()
				.kanal(mapKanal(varselMal.getKanal()))
				.tittel(varselMal.getVarselTittel())
				.foerstegangsTekst(varselMal.getFoerstegangsvarselTekst())
				.revarslingTekst(varselMal.getRevarslingTekst())
				.build();
	}

	private Set<KanalCode> mapKanaler(Set<String> preferertKanal) {
		Assert.notNull(preferertKanal, "preferertKanal is null");

		return preferertKanal.stream().map(this::mapKanal).collect(toSet());
	}

	private KanalCode mapKanal(String kanal) {
		Assert.hasText(kanal, "kanal is empty");

		return KanalCode.valueOf(kanal);
	}
}
