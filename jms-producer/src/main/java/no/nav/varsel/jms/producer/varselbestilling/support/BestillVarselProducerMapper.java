package no.nav.varsel.jms.producer.varselbestilling.support;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.ObjectFactory;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Parameter;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Person;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;

import javax.xml.bind.JAXBElement;
import java.util.function.Function;

/**
 * Maps from {@link no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo} to {@link no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselProducerMapper implements Function<VarselbestillingTo, JAXBElement<VarselMedHandling>> {

	private ObjectFactory objectFactory = new ObjectFactory();

	public VarselMedHandling map(VarselbestillingTo to) {
		VarselMedHandling varsel = new VarselMedHandling();

		varsel.setVarselbestillingId(to.getVarselbestillingId());

		Person mottaker = new Person();
		mottaker.setIdent(to.getMottakerFnr());
		varsel.setMottaker(mottaker);

		varsel.setVarseltypeId(to.getVarseltypeId());
		varsel.setVarselbestillingId(to.getVarselbestillingId());
		varsel.setReVarsel(to.isRevarsel());
		to.getParameters().entrySet().forEach(e -> {
			Parameter parameter = new Parameter();
			parameter.setKey(e.getKey());
			parameter.setValue(e.getValue());
			varsel.getParameterListe().add(parameter);
		});

		return varsel;
	}

	@Override
	public JAXBElement<VarselMedHandling> apply(VarselbestillingTo varselbestillingTo) {
		VarselMedHandling map = map(varselbestillingTo);
		return objectFactory.createVarselMedHandling(map);
	}
}
