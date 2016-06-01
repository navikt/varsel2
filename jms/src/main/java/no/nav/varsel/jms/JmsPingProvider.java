package no.nav.varsel.jms;

import com.google.common.collect.Maps;
import no.nav.varsel.config.support.QueueInfo;
import no.nav.varsel.domain.to.Ping;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Jms Ping Provider
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class JmsPingProvider implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(JmsPingProvider.class);

	@Resource
	private Map<QueueInfo, Queue> queueOverview;
	private Map<QueueInfo, Queue> filteredQueueOverview;

	@Inject
	private JmsTemplate jmsTemplate;

	public List<Ping> ping() {
		Map<QueueInfo, Queue> map = filteredQueueOverview;

		LOG.debug("tester køer {}", map.size());
		List<Ping> pings = new ArrayList<>();
		for (final QueueInfo queueInfo : map.keySet()) {
			Queue queue = map.get(queueInfo);
			final String queueName = getQueueName(queue);
			pings.add(new Ping(Ping.Type.Queue, queueInfo.getInternalName(), queueInfo.getDescription() ,queueName, () -> {
				try {
					checkQueue(queue);
				} catch (Exception e) {
					String errorMsg = "JMS Queue Browser failed to get queue: " + queueName;
					throw new UncategorizedJmsException(errorMsg, e);
				}
			}));
		}
		return pings;
	}

	private String getQueueName(Queue queue) {
		try {
			String queueName = queue.getQueueName();
			if (queueName.contains("/")) {
				queueName = StringUtils.substringAfterLast(queueName, "/");
			}
			return queueName;
		} catch (JMSException e) {
			LOG.error("Encountered error when looking up queueName", e);
			return "unknown";
		}
	}

	private Void checkQueue(final Queue queueName) {
		return jmsTemplate.browse(queueName,
				(session, browser) -> {
					browser.getQueue();
					LOG.debug("{} er ok", queueName);
					return null;
				}
		);
	}

	private Map<QueueInfo, Queue> removeForeignQueueManagerQueues(Map<QueueInfo, Queue> queueMap) {
		Map<QueueInfo, Queue> mqGateway01Queues = Maps.newHashMap(queueMap);
//		mqGateway01Queues.remove(FAGSYSTEM_STATUS_ENDRING_QUEUE);
		return mqGateway01Queues;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		filteredQueueOverview = removeForeignQueueManagerQueues(queueOverview);
	}

}
