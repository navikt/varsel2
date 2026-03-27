package no.nav.varsel.tvarsel001.jms.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import no.nav.varsel.config.NaisProperties;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.tvarsel001.jms.config.alias.ListenerProperties;
import no.nav.varsel.tvarsel001.jms.config.alias.MqGatewayProperties;
import no.nav.varsel.tvarsel001.service.service.support.BrukervarselMapper;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Clock;

import static no.nav.varsel.domain.Constants.NORGE_ZONE;

@EnableAutoConfiguration(exclude = {DataSourceTransactionManagerAutoConfiguration.class})
@Import({JmsConfig.class, BrukervarselMapper.class})
@Configuration
@EnableConfigurationProperties({
		ListenerProperties.class,
		MqGatewayProperties.class,
		NaisProperties.class,
		VarselProperties.class
})
public class JmsTestConfig {

	private final VarselProperties varselProperties;

	public JmsTestConfig(VarselProperties varselProperties) {
		this.varselProperties = varselProperties;
	}

	@Bean
	public Queue bestillServicemeldingQueue() {
		return new ActiveMQQueue(varselProperties.getQueues().getBestillServicemelding().getQueuename());
	}

	@Bean
	public Queue bestillServicemeldingFunksjonellFeilQueue() {
		return new ActiveMQQueue(varselProperties.getQueues().getBestillServicemelding().getFunkfeilQueuename());
	}

	@Bean
	public Queue backoutQueue() {
		return new ActiveMQQueue("backout_queue");
	}

	@Bean
	public Queue testReplyQueue() {
		return new ActiveMQQueue("test_reply_queue");
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public EmbeddedActiveMQ activeMQServer() {
		EmbeddedActiveMQ embeddedActiveMQ = new EmbeddedActiveMQ();
		embeddedActiveMQ.setConfigResourcePath("artemis-server.xml");
		return embeddedActiveMQ;
	}

	@Bean
	public ConnectionFactory connectionFactory(EmbeddedActiveMQ embeddedActiveMQ) {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("vm://0");
		JmsPoolConnectionFactory pooledFactory = new JmsPoolConnectionFactory();
		pooledFactory.setConnectionFactory(activeMQConnectionFactory);
		pooledFactory.setMaxConnections(1);
		return pooledFactory;
	}

	@Bean
	public Clock clock() {
		return Clock.system(NORGE_ZONE);
	}

}
