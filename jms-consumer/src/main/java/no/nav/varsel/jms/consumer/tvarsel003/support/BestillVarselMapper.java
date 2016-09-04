package no.nav.varsel.jms.consumer.tvarsel003.support;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Aktoer;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.AktoerId;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Parameter;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Person;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import no.nav.varsel.service.to.BestillVarselTo;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselMapper {

	public BestillVarselTo map(ObjectMessageWrapper<VarselMedHandling> varselBestilling) {
		VarselMedHandling varsel = varselBestilling.getObject();
		BestillVarselTo result = new BestillVarselTo();

		result.setVarselBestillingId(varsel.getVarselbestillingId());
		result.setRevarsling(varsel.isReVarsel());
		map(varsel.getMottaker(), result);
		result.setVarseltypeId(varsel.getVarseltypeId());
		result.setParameters(map(varsel.getParameterListe()));
		result.setUtloepstidspunkt(XmlGregorianConverter.toLocalDateTime(varsel.getUtloepstidspunkt()));
		result.setTestvarsel(getTestVarselValue(varselBestilling));
		return result;
	}

	private boolean getTestVarselValue(ObjectMessageWrapper<VarselMedHandling> varselBestilling) {
		try {
			return varselBestilling.getMessage().getBooleanProperty(BestillVarselTo.TESTVARSEL);
		} catch (JMSException jmse) {
			throw new  RuntimeException(jmse);
		}
	}


	private Map<String, String> map(List<Parameter> parameterListe) {
		HashMap<String, String> map = new HashMap<>();
		parameterListe.forEach(p -> map.put(p.getKey(), p.getValue()));
		return map;
	}

	private void map(Aktoer aktoer, BestillVarselTo to) {
		if (aktoer instanceof AktoerId) {
			to.setAktoerId(((AktoerId) aktoer).getAktoerId());
		} else if (aktoer instanceof Person) {
			to.setPersonIdent(((Person) aktoer).getIdent());
		}
	}
}
