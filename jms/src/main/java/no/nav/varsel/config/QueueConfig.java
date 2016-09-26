package no.nav.varsel.config;

import static no.nav.varsel.config.JmsConfig.getJndiObject;
import static no.nav.varsel.config.support.QueueInfo.BESTILL_SERVICEMELDING;
import static no.nav.varsel.config.support.QueueInfo.BESTILL_SERVICEMELDING_KONTAKTINFO;
import static no.nav.varsel.config.support.QueueInfo.BESTILL_VARSEL;
import static no.nav.varsel.config.support.QueueInfo.REVARSEL_STOPP;
import static no.nav.varsel.config.support.QueueInfo.VARSELUTSENDING;
import static no.nav.varsel.config.support.QueueInfo.VARSEL_KVITTERING;

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
	public Queue bestillVarselQueue() {
		return getQueue("java:/jboss/bestillVarsel");
	}

	@Bean
	public Queue revarselStoppQueue() {
		return getQueue("java:/jboss/revarselStopp");
	}

	@Bean
	public Queue bestillServicemeldingKontaktInfoQueue() {
		return getQueue("java:/jboss/bestillServicemeldingKontaktInfo");
	}

	@Bean
	public Map<QueueInfo, Queue> queueOverview() {
		HashMap<QueueInfo, Queue> map = Maps.newHashMap();
		map.put(BESTILL_SERVICEMELDING, bestillServicemeldingQueue());
		map.put(BESTILL_VARSEL, bestillVarselQueue());
		map.put(VARSEL_KVITTERING, varselKvitteringQueue());
		map.put(VARSELUTSENDING, varselutsendingQueue());
		map.put(REVARSEL_STOPP, revarselStoppQueue());
		map.put(BESTILL_SERVICEMELDING_KONTAKTINFO, bestillServicemeldingKontaktInfoQueue());
		return map;
	}

	static Queue getQueue(String jndi) {
		return getJndiObject(jndi, Queue.class);
	}
}
