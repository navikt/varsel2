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
import javax.jms.MessageFormatException;
import java.util.Collections;
import java.util.Enumeration;
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
		Enumeration<String> propertyNames;
		try {
			propertyNames = varselBestilling.getMessage().getPropertyNames();
		} catch (JMSException jmse) {
			throw new RuntimeException(jmse);
		}
		return Collections.list(propertyNames).
				stream().
				filter(propertyName -> propertyName != null && BestillVarselTo.TESTVARSEL.equals(propertyName.toUpperCase())).
				anyMatch(testvarselPropertyName -> {
					boolean result;
					try {
						result = varselBestilling.getMessage().getBooleanProperty(testvarselPropertyName);
					} catch (MessageFormatException mfe) {
						//Parameter Testvarsel with wrong type, for instance Long should not
						//put the message on the backout-queue, instead ignore the property
						result = false;
					} catch (JMSException jmse) {
						throw new RuntimeException(jmse);
					}
					return result;
				});
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
