package no.nav.varsel.jms.producer;


import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.ObjectFactory;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Varselutsending;
import no.nav.varsel.jms.producer.varselutsending.support.VarselutsendingMapper;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;
import javax.jms.Queue;

/**
 * Varselutsending Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingProducer {

	@Inject
	private JmsTemplate jmsTemplate;
	@Inject
	private Queue varselutsendingQueue;
	@Inject
	private VarselutsendingMapper mapper;

	private ObjectFactory objectFactory = new ObjectFactory();

	public void produce(VarselutsendingTo varselutsendingTo) {
		// TODO replace
		if ("feilMqUt".equals(varselutsendingTo.getVarslingstype())) {
			throw new RuntimeException("mq ut feil");
		}
		Varselutsending varselutsending = mapper.map(varselutsendingTo);
		jmsTemplate.convertAndSend(varselutsendingQueue, objectFactory.createVarselutsending(varselutsending));
	}
}
