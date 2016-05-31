package no.nav.varsel.jms.consumer;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
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
public class ConsumerManager {

	// Number of errors needed to stop a consumer
	public static final int DEFAULT_CONTEXT_SIZE = 20;
	// Number of seconds the "number of errors" defined in CONTEXT_SIZE needs to be within to stop a consumer
	public static final int DEFAULT_CONTEXT_TIME_SECONDS = 10;
	// Number of seconds to wait before restarting consumber
	public static final int DEFAULT_RESTART_TIME_SECONDS = 60 * 10;

	private int contextSize;
	private int contextTimeSeconds;
	private int restartTimeSeconds;

	public ConsumerManager() {
		contextSize = DEFAULT_CONTEXT_SIZE;
		contextTimeSeconds = DEFAULT_CONTEXT_TIME_SECONDS;
		restartTimeSeconds = DEFAULT_RESTART_TIME_SECONDS;
	}

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
				}
			}).start();
		}
	}

	public void setContextSize(int contextSize) {
		this.contextSize = contextSize;
	}

	public void setContextTimeSeconds(int contextTimeSeconds) {
		this.contextTimeSeconds = contextTimeSeconds;
	}

	public void setRestartTimeSeconds(int restartTimeSeconds) {
		this.restartTimeSeconds = restartTimeSeconds;
	}
}
