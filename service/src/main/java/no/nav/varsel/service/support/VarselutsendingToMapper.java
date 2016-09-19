package no.nav.varsel.service.support;

import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for Varselutsending
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingToMapper {

	public List<VarselutsendingTo> map(Varselbestilling varselbestilling) {
		LocalDateTime utlopTidspunkt = varselbestilling.getUtlopTidspunkt();
		Set<Varsel> varsels = varselbestilling.getVarsels();
		return mapVarsels(varselbestilling, utlopTidspunkt, varsels);
	}

	public List<VarselutsendingTo> mapVarsels(Varselbestilling varselbestilling, LocalDateTime utlopTidspunkt, Set<Varsel> varsels) {
		return varsels.stream().map(varsel -> {
			VarselutsendingTo to = new VarselutsendingTo();
			to.setUtloepstidspunkt(utlopTidspunkt);
			to.setVarseltypeId(varselbestilling.getVarseltypeId());
			to.setKanal(varsel.getKanal());
			to.setKontaktInformasjon(varsel.getKontaktInfo());
			if(varsel.getKanal().hasExternalUtsendingskanal()) {
				to.setMottaker(AktoerTo.newPersonIdent(varselbestilling.getFnr()));
			} else {
				to.setMottaker(AktoerTo.newAktoerId((varselbestilling.getAktorId())));
			}
			to.setVarselId(varsel.getVarselId());
			to.setVarselUrl(varsel.getVarselUrl());
			to.setVarselTekst(varsel.getVarselTekst());
			to.setVarselTittel(varsel.getVarselTittel());
			return to;
		}).collect(Collectors.toList());
	}
}
