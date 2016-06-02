package no.nav.varsel.jms.consumer;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.MessageListenerContainer;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages queue consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class JmsConsumerManager {

	@Value("${varsel.jms.consumer.error.context.size}")
	private Integer contextSize;
	@Value("${varsel.jms.consumer.error.context.time.seconds}")
	private Integer contextTimeSeconds;
	@Value("${varsel.jms.consumer.error.restart.delay.seconds}")
	private Integer restartTimeSeconds;

	private Map<JmsConsumer, Queue<LocalDateTime>> recentErrors = new ConcurrentHashMap<>();

	@Inject
	private JmsListenerEndpointRegistry endpointRegistry;

	public Queue<LocalDateTime> getErrorsFor(JmsConsumer jmsConsumer) {
		if (!recentErrors.containsKey(jmsConsumer)) {
			recentErrors.put(jmsConsumer, Queues.synchronizedQueue(EvictingQueue.create(contextSize)));
		}
		return recentErrors.get(jmsConsumer);
	}

	public void clearErrors() {
		recentErrors.clear();
	}

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

	public void registerError(JmsConsumer jmsConsumer) {
		Queue<LocalDateTime> errorsFor = getErrorsFor(jmsConsumer);
		errorsFor.add(LocalDateTime.now());
		if (errorsFor.size() == contextSize) {
			checkErrorStatus(errorsFor.peek(), jmsConsumer);
		}
	}

	private void checkErrorStatus(LocalDateTime oldest, JmsConsumer jmsConsumer) {
		if (oldest.isAfter(LocalDateTime.now().minusSeconds(contextTimeSeconds))) {
			stop(jmsConsumer);
			new Thread(() -> {
				try {
					Thread.sleep(1000L * restartTimeSeconds);
				} catch (InterruptedException e) {
					// ignore
				} finally {
					start(jmsConsumer);
					getErrorsFor(jmsConsumer).clear();
				}
			}).start();
		}
	}

	void setContextSize(int contextSize) {
		this.contextSize = contextSize;
	}

	void setContextTimeSeconds(int contextTimeSeconds) {
		this.contextTimeSeconds = contextTimeSeconds;
	}

	void setRestartTimeSeconds(int restartTimeSeconds) {
		this.restartTimeSeconds = restartTimeSeconds;
	}
}
