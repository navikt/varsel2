package no.nav.varsel.jms.producer;


import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.jms.producer.varselbestilling.support.BestillVarselProducerMapper;
import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;

/**
 * Varselbestilling JMS Producer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingProducer {

	@Inject
	private JmsTemplate jmsTemplate;
	@Inject
	private Queue bestillVarselQueue;
	@Inject
	private BestillVarselProducerMapper bestillVarselProducerMapper;

	public void produce(VarselbestillingTo varselbestillingTo) {
		JAXBElement<VarselMedHandling> varselMedHandling = bestillVarselProducerMapper.apply(varselbestillingTo);
		jmsTemplate.convertAndSend(bestillVarselQueue, varselMedHandling);
	}
}
