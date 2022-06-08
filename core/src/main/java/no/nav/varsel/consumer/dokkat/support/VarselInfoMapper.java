package no.nav.varsel.consumer.dokkat.support;

import static java.util.stream.Collectors.toSet;
import static no.nav.varsel.consumer.dokkat.to.VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo;

import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.dokkat.schemas.tkat021.VarselMalRestTo;
import no.nav.varsel.consumer.dokkat.to.VarselMalTo;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.consumer.dokkat.to.VarselInfoTo;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Mapper for VarselInfo
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoMapper {

	public VarselInfoTo map(VarselInfoRestTo varselInfo) {
		Assert.notNull(varselInfo, "varselInfo is null");
		Set<VarselMalRestTo> varselmals = varselInfo.getVarselmals();

		return aVarselInfoTo()
				.varseltypeId(varselInfo.getVarseltypeId())
				.varselNavn(varselInfo.getVarselNavn())
				.varselForDistKanal(varselInfo.getVarselForDistribusjonKanal())
				.varselKategori(varselInfo.getVarselKategori())
				.inaktiv(varselInfo.getInaktiv())
				.revarslingIntervall(varselInfo.getRevarslingIntervall())
				.antallRevarsling(varselInfo.getAntallRevarslinger())
				.varselUrl(varselInfo.getVarselURL())
				.preferertKanal(mapKanals(varselInfo.getPreferertKanal()))
				.maler(varselmals == null ? null : varselmals.stream().map(this::mapMal).collect(toSet()))
				.build();
	}

	private VarselMalTo mapMal(VarselMalRestTo varselMal) {
		return VarselMalTo.VarselMalToBuilder.aVarselMalTo()
				.kanal(mapKanal(varselMal.getKanal()))
				.tittel(varselMal.getVarselTittel())
				.foerstegangsTekst(varselMal.getFoerstegangsvarselTekst())
				.revarslingTekst(varselMal.getRevarslingTekst())
				.build();
	}

	private Set<KanalCode> mapKanals(Set<String> preferertKanal) {
		Assert.notNull(preferertKanal, "preferertKanal is null");
		return preferertKanal.stream().map(this::mapKanal).collect(toSet());
	}

	private KanalCode mapKanal(String kanal) {
		Assert.hasText(kanal, "kanal is empty");
		return KanalCode.valueOf(kanal);
	}
}
