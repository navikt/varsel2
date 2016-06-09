package no.nav.varsel.wsconsumer.dokkat.support;

import static java.util.stream.Collectors.toSet;
import static no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo;
import static no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo.VarselMalToBuilder.aVarselMalTo;

import no.nav.dokkat.schemas.tkat021.VarselInfo;
import no.nav.dokkat.schemas.tkat021.VarselMal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Mapper for VarselInfo
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoMapper {

	public VarselInfoTo map(VarselInfo varselInfo) {
		Assert.notNull(varselInfo, "varselInfo is null");
		Set<VarselMal> varselmals = varselInfo.getVarselmals();

		return aVarselInfoTo()
				.varslingstype(varselInfo.getVarslingstype())
				.varselForDistrKanal(varselInfo.getVarselForDistribusjonKanal())
				.varselKategori(varselInfo.getVarselKategori())
				.inaktiv(varselInfo.getInaktiv())
				.revarslingIntervall(varselInfo.getRevarslingIntervall())
				.antallRevarsling(varselInfo.getAntallRevarslinger())
				.preferertKanal(mapKanals(varselInfo.getPreferertKanal()))
				.maler(varselmals == null ? null : varselmals.stream().map(this::mapMal).collect(toSet()))
				.build();
	}

	private VarselMalTo mapMal(VarselMal varselMal) {
		return aVarselMalTo()
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
