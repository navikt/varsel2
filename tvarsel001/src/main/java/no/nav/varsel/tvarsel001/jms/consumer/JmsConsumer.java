package no.nav.varsel.tvarsel001.jms.consumer;

import static no.nav.varsel.tvarsel001.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;

public enum JmsConsumer {
	BESTILL_SERVICEMELDING(BESTILL_SERVICEMELDING_NAME, "tvarsel001");

	private final String consumerName;
	private final String serviceName;

	JmsConsumer(String consumerName, String serviceName) {
		this.consumerName = consumerName;
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
	}

	@Override
	public String toString() {
		return String.format("consumerName=%s serviceName=%s", consumerName, serviceName);
	}
}
