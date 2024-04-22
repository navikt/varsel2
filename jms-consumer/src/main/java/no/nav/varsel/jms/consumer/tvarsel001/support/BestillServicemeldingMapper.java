package no.nav.varsel.jms.consumer.tvarsel001.support;

import jakarta.jms.JMSException;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLAktoer;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLAktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLParameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLPersonIdent;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.service.to.BestillVarselTo;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.varsel.domain.utility.DateTimeConverter.toLocalDateTime;

@Component
public class BestillServicemeldingMapper {

	public BestillVarselTo map(ObjectMessageWrapper<XMLVarsel> varselWithMessage) {
		Assert.notNull(varselWithMessage, "varselWithMessage er null");
		BestillVarselTo to = new BestillVarselTo();

		XMLVarsel varsel = varselWithMessage.getObject();
		map(varsel.getMottaker(), to);
		to.setVarseltypeId(varsel.getVarslingstype() == null ? null :
				varsel.getVarslingstype().getValue());
		to.setUtloepstidspunkt(toLocalDateTime(varsel.getUtloepstidspunkt()));
		to.setParameters(map(varsel.getParameterListes()));
		to.setTestvarsel(getTestVarselValue(varselWithMessage));

		return to;
	}

	private boolean getTestVarselValue(ObjectMessageWrapper<XMLVarsel> varsel) {
		try {
			return varsel.getMessage().getBooleanProperty(BestillVarselTo.TESTVARSEL);
		} catch (JMSException e) {
			return false;
		}
	}

	private Map<String, String> map(List<XMLParameter> parameterListe) {
		HashMap<String, String> map = new HashMap<>();
		parameterListe.forEach(p -> map.put(p.getKey(), p.getValue()));
		return map;
	}

	private void map(XMLAktoer aktoer, BestillVarselTo to) {
		if (aktoer instanceof XMLAktoerId) {
			to.setAktoerId(((XMLAktoerId) aktoer).getAktoerId());
		} else if (aktoer instanceof XMLPersonIdent) {
			to.setPersonIdent(((XMLPersonIdent) aktoer).getPersonIdent());
		}
	}
}
