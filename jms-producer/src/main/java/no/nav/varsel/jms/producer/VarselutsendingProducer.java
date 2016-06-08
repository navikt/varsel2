package no.nav.varsel.jms.producer;


import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.ObjectFactory;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.jms.producer.varselutsending.support.VarselutsendingMapper;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;
import javax.jms.Queue;

/**
 * Varselutsending JMS Producer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingProducer {

	public static final String FEIL_MQ_UT = "varsel_test_feil";

	@Inject
	private JmsTemplate jmsTemplate;
	@Inject
	private Queue varselutsendingQueue;
	@Inject
	private VarselutsendingMapper mapper;

	private ObjectFactory objectFactory = new ObjectFactory();

	public void produce(VarselutsendingTo varselutsendingTo) {
		// TODO replace with better mocking
		if (FEIL_MQ_UT.equals(varselutsendingTo.getVarslingstype())) {
			throw new RuntimeException("mq ut feil");
		}
		Varselutsending varselutsending = mapper.map(varselutsendingTo);
		jmsTemplate.convertAndSend(varselutsendingQueue, objectFactory.createVarselutsending(varselutsending));
	}
}
