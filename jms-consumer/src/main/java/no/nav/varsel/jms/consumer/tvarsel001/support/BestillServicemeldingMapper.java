package no.nav.varsel.jms.consumer.tvarsel001.support;

import static no.nav.varsel.domain.auxillary.XmlGregorianConverter.toLocalDateTime;

import no.nav.melding.virksomhet.varsel.v1.varsel.Aktoer;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.PersonIdent;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import no.nav.varsel.service.tvarsel001.to.BestillServicemeldingTo;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingMapper {

	public BestillServicemeldingTo map(Varsel varsel) {
		Assert.notNull(varsel, "Varsel er null");
		BestillServicemeldingTo to = new BestillServicemeldingTo();

		map(varsel.getMottaker(), to);
		to.setVarslingstype(varsel.getVarslingstype() == null ? null :
				varsel.getVarslingstype().getValue());
		to.setUtloepstidspunkt(toLocalDateTime(varsel.getUtloepstidspunkt()));
		to.setParameters(map(varsel.getParameterListe()));

		return to;
	}

	private Map<String, String> map(List<Parameter> parameterListe) {
		HashMap<String, String> map = new HashMap<>();
		parameterListe.stream().forEach(p -> map.put(p.getKey(), p.getValue()));
		return map;
	}

	private void map(Aktoer aktoer, BestillServicemeldingTo to) {
		if (aktoer instanceof AktoerId) {
			to.setAktoerId(((AktoerId) aktoer).getAktoerId());
		} else if (aktoer instanceof PersonIdent) {
			to.setPersonIdent(((PersonIdent) aktoer).getPersonIdent());
		}
	}
}
