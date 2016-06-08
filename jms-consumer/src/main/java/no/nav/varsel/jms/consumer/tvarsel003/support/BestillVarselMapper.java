package no.nav.varsel.jms.consumer.tvarsel003.support;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Aktoer;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.AktoerId;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.NorskIdent;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Parameter;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Person;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import no.nav.varsel.service.to.BestillVarselTo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselMapper {

	public BestillVarselTo map(VarselMedHandling varsel) {
		BestillVarselTo to = new BestillVarselTo();

		to.setVarselBestillingId(varsel.getVarselbestillingId());
		to.setRevarsling(varsel.isRevarsling());
		map(varsel.getMottaker(), to);
		to.setVarslingstype(varsel.getVarslingstype() == null ? null : varsel.getVarslingstype().getValue());
		to.setParameters(map(varsel.getParameterListe()));
		to.setUtloepstidspunkt(XmlGregorianConverter.toLocalDateTime(varsel.getUtloepstidspunkt()));

		return to;
	}

	private Map<String, String> map(List<Parameter> parameterListe) {
		HashMap<String, String> map = new HashMap<>();
		parameterListe.stream().forEach(p -> map.put(p.getKey(), p.getValue()));
		return map;
	}


	private void map(Aktoer aktoer, BestillVarselTo to) {
		if (aktoer instanceof AktoerId) {
			to.setAktoerId(((AktoerId) aktoer).getAktoerId());
		} else if (aktoer instanceof Person) {
			NorskIdent norskIdent = ((Person) aktoer).getPersonIdent();
			to.setPersonIdent(norskIdent.getIdent());
			to.setPersonidentType(norskIdent.getType() == null ? null : norskIdent.getType().getValue());
		}
	}
}
