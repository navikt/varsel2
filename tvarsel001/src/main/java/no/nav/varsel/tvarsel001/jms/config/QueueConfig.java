package no.nav.varsel.tvarsel001.jms.config;

import com.ibm.mq.jakarta.jms.MQQueue;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import no.nav.varsel.config.VarselProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

	private final VarselProperties varselProperties;

	public QueueConfig(VarselProperties varselProperties) {
		this.varselProperties = varselProperties;
	}

	@Bean
	public Queue bestillServicemeldingQueue() throws JMSException {
		return new MQQueue(varselProperties.getQueues().getBestillServicemelding().getQueuename());
	}

	@Bean
	public Queue bestillServicemeldingFunksjonellFeilQueue() throws JMSException {
		return new MQQueue(varselProperties.getQueues().getBestillServicemelding().getFunkfeilQueuename());
	}
}
