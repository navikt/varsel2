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

	private static final String VM_LOCALHOST = "vm://localhost";

	@BeforeEach
	public void setUp() {
		setProperty("varsel.serviceuser.username", "srvvarsel");
		setProperty("varsel.serviceuser.password", "passord");
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public EmbeddedActiveMQ activeMQServer() {
		EmbeddedActiveMQ embeddedActiveMQ = new EmbeddedActiveMQ();
		embeddedActiveMQ.setConfigResourcePath("artemis-server.xml");
		return embeddedActiveMQ;
	}

	// avhengig av EmbeddedActiveMQ slik at server er startet før klient forsøker lage koblinger
	@Bean
	public ConnectionFactory activemqConnectionFactory(EmbeddedActiveMQ embeddedActiveMQ) {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("vm://0");
		JmsPoolConnectionFactory pooledFactory = new JmsPoolConnectionFactory();
		pooledFactory.setConnectionFactory(activeMQConnectionFactory);
		pooledFactory.setMaxConnections(1);
		return pooledFactory;
	}

	@Bean
	public Queue replyQueue() throws JMSException {
		return new MQQueue("reply_queue");
	}

	@Bean
	public Queue backoutQueue() {
		return new ActiveMQQueue("backout_queue");
	}

}
