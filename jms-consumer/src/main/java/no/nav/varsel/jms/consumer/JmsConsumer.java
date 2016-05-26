package no.nav.varsel.jms.consumer;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.VARSEL_KVITTERING_NAME;

/**
 * Available JMS Consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public enum JmsConsumer {
	BESTILL_SERVICEMELDING(BESTILL_SERVICEMELDING_NAME),
	VARSEL_KVITTERING(VARSEL_KVITTERING_NAME);

	private final String consumerName;

	JmsConsumer(String name) {
		consumerName = name;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public class ConsumerNames {
		public static final String BESTILL_SERVICEMELDING_NAME = "bestillServicemeldingConsumer";
		public static final String VARSEL_KVITTERING_NAME = "varselKvitteringConsumer";
	}

}
