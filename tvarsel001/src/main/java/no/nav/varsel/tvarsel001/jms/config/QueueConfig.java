package no.nav.varsel.tvarsel001.jms.config;

import com.ibm.mq.jakarta.jms.MQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.jms.JMSException;
import jakarta.jms.Queue;

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
}
