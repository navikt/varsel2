package no.nav.varsel.jms.consumer;

import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.MessageListenerContainer;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Manages queue consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ConsumerManager {

	@Inject
	private JmsListenerEndpointRegistry endpointRegistry;

	public void stop(JmsConsumer consumer) {
		getListener(consumer).stop();
	}

	public void start(JmsConsumer consumer) {
		getListener(consumer).start();
	}

	public void stopAll() {
		getListeners().forEach(MessageListenerContainer::stop);
	}

	public void startAll() {
		getListeners().forEach(MessageListenerContainer::start);
	}

	public MessageListenerContainer getListener(JmsConsumer consumer) {
		return endpointRegistry.getListenerContainer(consumer.getConsumerName());
	}

	public Collection<MessageListenerContainer> getListeners() {
		return endpointRegistry.getListenerContainers();
	}
}
