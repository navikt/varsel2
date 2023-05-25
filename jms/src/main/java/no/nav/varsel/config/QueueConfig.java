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
}
