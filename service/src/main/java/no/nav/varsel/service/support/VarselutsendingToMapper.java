package no.nav.varsel.service.support;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Varselutsending
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingToMapper {

	public List<VarselutsendingTo> map(Varselbestilling varselbestilling, AktoerTo aktoer) {
		return varselbestilling.getVarsels().stream().map(varsel -> {
			VarselutsendingTo to = new VarselutsendingTo();
			to.setUtloepstidspunkt(varselbestilling.getUtlopTidspunkt());
			to.setVarslingstype(varselbestilling.getVarslingstype());
			to.setKanal(varsel.getKanal());
			to.setMottaker(aktoer);
			to.setVarselId(varsel.getVarselId());
			to.setVarselUrl(varsel.getVarselUrl());
			to.setVarselTekst(varsel.getVarselTekst());
			to.setVarselTittel(varsel.getVarselTittel());
			return to;
		}).collect(Collectors.toList());
	}
}
