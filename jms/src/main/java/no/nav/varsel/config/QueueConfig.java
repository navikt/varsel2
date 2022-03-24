package no.nav.varsel.config;

import com.google.common.collect.Maps;
import com.ibm.mq.jms.MQQueue;
import no.nav.varsel.config.support.QueueInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

import static no.nav.varsel.config.support.QueueInfo.BESTILL_SERVICEMELDING;
import static no.nav.varsel.config.support.QueueInfo.BESTILL_SERVICEMELDING_KONTAKTINFO;
import static no.nav.varsel.config.support.QueueInfo.VARSELUTSENDING;
import static no.nav.varsel.config.support.QueueInfo.VARSEL_KVITTERING;

/**
 * Spring config for queues
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class QueueConfig {

	@Bean
	public Queue bestillServicemeldingQueue(@Value("${bestillservicemelding.queuename}") String bestillServicemeldingQueueName) throws JMSException {
		MQQueue queue = new MQQueue(bestillServicemeldingQueueName);
		return queue;
	}

	@Bean
	public Queue varselKvitteringQueue(@Value("${varselkvittering.queuename}") String varselKvitteringQueueName) throws JMSException {
		MQQueue queue = new MQQueue(varselKvitteringQueueName);
		return queue;
	}

	@Bean
	public Queue varselutsendingQueue(@Value("${varselutsending.queuename}") String varselutsendingQueueName) throws JMSException {
		MQQueue queue = new MQQueue(varselutsendingQueueName);
		return queue;
	}

	@Bean
	public Queue bestillServicemeldingKontaktInfoQueue(@Value("${bestillservicemeldingkontaktinfo.queuename}") String bestillServicemeldingKontaktInfoQueueName) throws JMSException {
		MQQueue queue = new MQQueue(bestillServicemeldingKontaktInfoQueueName);
		return queue;
	}

	@Bean
	public Map<QueueInfo, Queue> queueOverview(
			@Qualifier("bestillServicemeldingQueue") Queue bestillServicemeldingQueue,
			@Qualifier("varselKvitteringQueue") Queue varselKvitteringQueue,
			@Qualifier("varselutsendingQueue") Queue varselutsendingQueue,
			@Qualifier("bestillServicemeldingKontaktInfoQueue") Queue bestillServicemeldingKontaktInfoQueue) {
		HashMap<QueueInfo, Queue> map = Maps.newHashMap();
		map.put(BESTILL_SERVICEMELDING, bestillServicemeldingQueue);
		map.put(VARSEL_KVITTERING, varselKvitteringQueue);
		map.put(VARSELUTSENDING, varselutsendingQueue);
		map.put(BESTILL_SERVICEMELDING_KONTAKTINFO, bestillServicemeldingKontaktInfoQueue);
		return map;
	}

}
