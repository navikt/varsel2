package no.nav.varsel.config;

import com.ibm.mq.jakarta.jms.MQQueue;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import no.nav.varsel.config.alias.ListenerProperties;
import no.nav.varsel.config.alias.MqGatewayProperties;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.junit.jupiter.api.BeforeEach;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static java.lang.System.setProperty;

@EnableAutoConfiguration(exclude = {DataSourceTransactionManagerAutoConfiguration.class})
@Import({JmsConfig.class})
@Configuration
@EnableConfigurationProperties({
		ListenerProperties.class,
		MqGatewayProperties.class
})
public class JmsTestConfig {

	@BeforeEach
	public void setUp() {
		setProperty("varsel.serviceuser.username", "srvvarsel");
		setProperty("varsel.serviceuser.password", "passord");
	}
	@Bean
	public Queue bestillServicemeldingQueue(@Value("${bestillservicemelding.queuename}") String bestillServicemeldingQueueName) {
		return new ActiveMQQueue(bestillServicemeldingQueueName);
	}

	@Bean
	public Queue bestillServicemeldingFunksjonellFeilQueue(@Value("${bestillservicemelding.funkfeil.queuename}") String bestillServicemeldingFunksjonellFeilQueueName) throws JMSException {
		return new MQQueue(bestillServicemeldingFunksjonellFeilQueueName);
	}

	@Bean
	public Queue backoutQueue() {
		return new ActiveMQQueue("backout_queue");
	}



	@Bean(initMethod = "start", destroyMethod = "stop")
	public EmbeddedActiveMQ activeMQServer() {
		EmbeddedActiveMQ embeddedActiveMQ = new EmbeddedActiveMQ();
		embeddedActiveMQ.setConfigResourcePath("artemis-server.xml");
		return embeddedActiveMQ;
	}

	@Bean
	public ConnectionFactory activemqConnectionFactory(EmbeddedActiveMQ embeddedActiveMQ) {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("vm://0");
		JmsPoolConnectionFactory pooledFactory = new JmsPoolConnectionFactory();
		pooledFactory.setConnectionFactory(activeMQConnectionFactory);
		pooledFactory.setMaxConnections(1);
		return pooledFactory;
	}

}
