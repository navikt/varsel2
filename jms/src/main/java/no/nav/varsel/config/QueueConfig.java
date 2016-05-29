package no.nav.varsel.config;

import static no.nav.varsel.config.JmsConfig.getJndiObject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

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

	public static Queue getQueue(String jndi) {
		return getJndiObject(jndi, Queue.class);
	}
}
