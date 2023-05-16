package no.nav.varsel.config;

import com.ibm.mq.jms.MQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.JMSException;
import javax.jms.Queue;

@Configuration
public class QueueConfig {

	@Bean
	public Queue bestillServicemeldingQueue(@Value("${bestillservicemelding.queuename}") String bestillServicemeldingQueueName) throws JMSException {
		return new MQQueue(bestillServicemeldingQueueName);
	}

	@Bean
	public Queue bestillServicemeldingFunksjonellFeilQueue(@Value("${bestillservicemelding.funkfeil.queuename}") String bestillServicemeldingFunksjonellFeilQueueName) throws JMSException {
		return new MQQueue(bestillServicemeldingFunksjonellFeilQueueName);
	}

	@Bean
	public Queue bestillServicemeldingKontaktInfoQueue(@Value("${bestillservicemeldingkontaktinfo.queuename}") String bestillServicemeldingKontaktInfoQueueName) throws JMSException {
		return new MQQueue(bestillServicemeldingKontaktInfoQueueName);
	}

	@Bean
	public Queue bestillServicemeldingKontaktInfoFunksjonellFeilQueue(@Value("${bestillservicemeldingkontaktinfo.funkfeil.queuename}") String bestillServicemeldingKontaktInfoFunksjonellFeilQueueName) throws JMSException {
		return new MQQueue(bestillServicemeldingKontaktInfoFunksjonellFeilQueueName);
	}
}
