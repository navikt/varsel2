package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.varsel.domain.object.Varselbestilling;

import java.util.List;

public class VarselutsendingMapper {

	public List<Varselutsending> map(Varselbestilling varselbestilling) {
		return varselbestilling.getVarsels().stream()
				.map(varsel -> Varselutsending.builder()
						.kanal(varsel.getKanal())
						.varselUrl(varsel.getVarselUrl())
						.varselTekst(varsel.getVarselTekst())
						.varselTittel(varsel.getVarselTittel())
						.build()
				).toList();
	}
}
