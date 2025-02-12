package no.nav.varsel.tvarsel001.jms.consumer;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import no.nav.varsel.config.VarselProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JmsConsumerManager {

	public static final Logger LOG = LoggerFactory.getLogger(JmsConsumerManager.class);

	private Integer contextSize;
	private Integer contextTimeSeconds;
	private Integer restartTimeSeconds;

	private Map<JmsConsumer, Queue<LocalDateTime>> recentErrors = new ConcurrentHashMap<>();

	private JmsListenerEndpointRegistry endpointRegistry;

	public JmsConsumerManager(VarselProperties varselProperties,
							  JmsListenerEndpointRegistry endpointRegistry) {
		this.endpointRegistry = endpointRegistry;
		this.contextSize = varselProperties.getJms().getConsumerErrorContextSize();
		this.contextTimeSeconds = varselProperties.getJms().getConsumerErrorContextTimeSeconds();
		this.restartTimeSeconds = varselProperties.getJms().getConsumerErrorRestartDelaySeconds();
	}

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

	public synchronized void registerError(JmsConsumer jmsConsumer) {
		Queue<LocalDateTime> errorsFor = getErrorsFor(jmsConsumer);
		errorsFor.add(LocalDateTime.now());
		if (errorsFor.size() == contextSize) {
			checkErrorStatus(errorsFor.peek(), jmsConsumer);
		}
	}

	private void checkErrorStatus(LocalDateTime oldest, JmsConsumer jmsConsumer) {
		LocalDateTime now = LocalDateTime.now();
		if (oldest.isAfter(now.minusSeconds(contextTimeSeconds))) {
			LOG.warn("Shutting down Jms Consumer {} for {} seconds based on {} errors in the last {}",
					jmsConsumer, restartTimeSeconds, contextSize, diff(oldest, now));
			stop(jmsConsumer);
			new Thread(() -> {
				try {
					Thread.sleep(1000L * restartTimeSeconds);
				} catch (InterruptedException e) {
					LOG.trace("sleep interrupted", e);
				} finally {
					start(jmsConsumer);
					LOG.info("Starting {} after {} seconds downtime", jmsConsumer, restartTimeSeconds);
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

	private String diff(LocalDateTime from, LocalDateTime to) {
		long mins = ChronoUnit.MINUTES.between(from, to);
		long seconds = ChronoUnit.SECONDS.between(from, to.minusMinutes(mins));
		return String.format("%d minutes %d seconds", mins, seconds);
	}
}
