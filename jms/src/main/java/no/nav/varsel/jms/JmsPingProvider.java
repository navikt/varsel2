package no.nav.varsel.jms;

import no.nav.varsel.config.support.QueueInfo;
import no.nav.varsel.domain.to.Ping;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JmsPingProvider {

	private static final Logger LOG = LoggerFactory.getLogger(JmsPingProvider.class);

	@Resource
	private Map<QueueInfo, Queue> queueOverview;

	@Inject
	private JmsTemplate jmsTemplate;

	public List<Ping> ping() {
		Map<QueueInfo, Queue> map = queueOverview;

		LOG.debug("tester køer {}", map.size());
		List<Ping> pings = new ArrayList<>();
		for (Map.Entry<QueueInfo, Queue> entry : map.entrySet()) {
			QueueInfo queueInfo = entry.getKey();
			Queue queue = entry.getValue();
			String queueName = getQueueName(queue);
			Runnable pinger = null;
			if (!queueInfo.isRemote()) {
				pinger = () -> {
					try {
						checkQueue(queue);
					} catch (Exception e) {
						String errorMsg = "JMS Queue Browser failed to get queue: " + queueName;
						throw new UncategorizedJmsException(errorMsg, e);
					}
				};
			}
			Ping.Type type = queueInfo.isRemote() ? Ping.Type.RemoteQueue : Ping.Type.Queue;
			pings.add(new Ping(type, queueInfo.getInternalName(), queueInfo.getDescription(), queueName, pinger));
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

}
