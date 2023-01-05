package no.nav.varsel.jms.consumer.tvarsel001.support;

import no.nav.melding.virksomhet.varsel.v1.varsel.Aktoer;
import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.PersonIdent;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.service.to.BestillVarselTo;
import org.springframework.util.Assert;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;

public class BestillServicemeldingMapper {
	public BestillVarselTo map(ObjectMessageWrapper<Varsel> varselWithMessage) {
		Assert.notNull(varselWithMessage, "varselWithMessage er null");
		BestillVarselTo to = new BestillVarselTo();

		Varsel varsel = varselWithMessage.getObject();
		map(varsel.getMottaker(), to);
		to.setVarseltypeId(varsel.getVarslingstype() == null ? null :
				varsel.getVarslingstype().getValue());
		to.setUtloepstidspunkt(toLocalDateTime(varsel.getUtloepstidspunkt()));
		to.setParameters(map(varsel.getParameterListe()));
		to.setTestvarsel(getTestVarselValue(varselWithMessage));
		return to;
	}

	private boolean getTestVarselValue(ObjectMessageWrapper<Varsel> varsel) {
		try {
			return varsel.getMessage().getBooleanProperty(BestillVarselTo.TESTVARSEL);
		} catch (JMSException e) {
			return false;
		}
	}

	private Map<String, String> map(List<Parameter> parameterListe) {
		HashMap<String, String> map = new HashMap<>();
		parameterListe.stream().forEach(p -> map.put(p.getKey(), p.getValue()));
		return map;
	}

	private void map(Aktoer aktoer, BestillVarselTo to) {
		if (aktoer instanceof AktoerId) {
			to.setAktoerId(((AktoerId) aktoer).getAktoerId());
		} else if (aktoer instanceof PersonIdent) {
			to.setPersonIdent(((PersonIdent) aktoer).getPersonIdent());
		}
	}
}
