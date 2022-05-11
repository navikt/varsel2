package no.nav.varsel.service.support;

import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;

import java.util.List;
import java.util.Set;

public class VarselutsendingMapper {

	public List<Varselutsending> map(Varselbestilling varselbestilling) {
		return mapVarsler(varselbestilling.getVarsels());
	}

	public List<Varselutsending> mapVarsler(Set<Varsel> varsler) {
		return varsler.stream().map(varsel -> Varselutsending.builder()
				.kanal(varsel.getKanal())
				.varselUrl(varsel.getVarselUrl())
				.varselTekst(varsel.getVarselTekst())
				.varselTittel(varsel.getVarselTittel())
				.build()
		).toList();
	}
}
