package no.nav.varsel.config;

import com.ibm.mq.jms.MQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * Spring config for queues
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class QueueConfig {

	@Bean
	public Queue bestillServicemeldingQueue(@Value("${bestillservicemelding.queuename}") String bestillServicemeldingQueueName) throws JMSException {
		return new MQQueue(bestillServicemeldingQueueName);
	}

	@Bean
	public Queue varselKvitteringQueue(@Value("${varselkvittering.queuename}") String varselKvitteringQueueName) throws JMSException {
		return new MQQueue(varselKvitteringQueueName);
	}

	@Bean
	public Queue varselutsendingQueue(@Value("${varselutsending.queuename}") String varselutsendingQueueName) throws JMSException {
		return new MQQueue(varselutsendingQueueName);
	}

	@Bean
	public Queue bestillServicemeldingKontaktInfoQueue(@Value("${bestillservicemeldingkontaktinfo.queuename}") String bestillServicemeldingKontaktInfoQueueName) throws JMSException {
		return new MQQueue(bestillServicemeldingKontaktInfoQueueName);
	}

}
