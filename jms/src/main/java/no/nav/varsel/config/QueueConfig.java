package no.nav.varsel.config;

import static no.nav.varsel.ServiceConfig.getJndiObject;

import org.springframework.context.annotation.Bean;

import javax.jms.Queue;

/**
 * Spring config for queues
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class QueueConfig {
	@Bean
	public Queue bestillServicemelding() {
		return getJndiObject("java:/jboss/bestillServicemelding", Queue.class);
	}

	@Bean
	public Queue varselKvittering() {
		return getJndiObject("java:/jboss/varselKvittering", Queue.class);
	}
}
