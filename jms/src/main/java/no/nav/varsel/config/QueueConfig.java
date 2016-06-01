package no.nav.varsel.config;

import static no.nav.varsel.config.JmsConfig.getJndiObject;
import static no.nav.varsel.config.support.QueueInfo.*;

import com.google.common.collect.Maps;
import no.nav.varsel.config.support.QueueInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring config for queues
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class QueueConfig {

	@Bean
	public Queue bestillServicemeldingQueue() {
		return getQueue("java:/jboss/bestillServicemelding");
	}

	@Bean
	public Queue varselKvitteringQueue() {
		return getQueue("java:/jboss/varselKvittering");
	}

	@Bean
	public Queue varselutsendingQueue() {
		return getQueue("java:/jboss/varselutsending");
	}

	@Bean
	public Map<QueueInfo, Queue> queueOverview(Queue bestillServicemeldingQueue,
											   Queue varselKvitteringQueue,
											   Queue varselutsendingQueue) {
		HashMap<QueueInfo, Queue> map = Maps.newHashMap();
		map.put(BESTILL_SERVICEMELDING, bestillServicemeldingQueue);
		map.put(VARSEL_KVITTERING, varselKvitteringQueue);
		map.put(VARSELUTSENDING, varselutsendingQueue);
		return map;
	}

	static Queue getQueue(String jndi) {
		return getJndiObject(jndi, Queue.class);
	}
}
