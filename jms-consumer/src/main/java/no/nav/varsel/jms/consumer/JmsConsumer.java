package no.nav.varsel.jms.consumer;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.REVARSEL_STOPP_NAME;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.VARSEL_KVITTERING_NAME;

/**
 * Available JMS Consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public enum JmsConsumer {
	BESTILL_SERVICEMELDING(BESTILL_SERVICEMELDING_NAME, "tvarsel001"),
	VARSEL_KVITTERING(VARSEL_KVITTERING_NAME, "tvarsel002"),
	REVARSEL_STOPP(REVARSEL_STOPP_NAME, "tvarsel004");

	private final String consumerName;
	private final String serviceName;

	JmsConsumer(String name, String serviceName) {
		this.consumerName = name;
		this.serviceName = serviceName;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public static class ConsumerNames {
		public static final String BESTILL_SERVICEMELDING_NAME = "bestillServicemeldingConsumer";
		public static final String VARSEL_KVITTERING_NAME = "varselKvitteringConsumer";
		public static final String REVARSEL_STOPP_NAME = "StoppReVarselConsumer";
	}

	@Override
	public String toString() {
		return String.format("consumerName=%s serviceName=%s", consumerName, serviceName);
	}
}
