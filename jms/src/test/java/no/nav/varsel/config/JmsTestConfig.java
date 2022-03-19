package no.nav.varsel.config;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.ibm.mq.jms.MQQueue;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Test Config for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableAutoConfiguration(exclude = {DataSourceTransactionManagerAutoConfiguration.class})
@Import({JmsConfig.class})
@Configuration
public class JmsTestConfig {

	private static final String VM_LOCALHOST = "vm://localhost";

	@Before
	public void setUp() {
		setProperty("varsel.serviceuser.username", "srvvarsel");
		setProperty("varsel.serviceuser.password", "passord");
	}

	/**
	 * Using the same username/password-wrapper as in production to ensure it delegates down to XAConnectionFactory
	 */
	@Bean
	public ConnectionFactory connectionFactory(ConnectionFactory atomikosConnectionFactoryBean) throws JMSException {
		UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
		adapter.setTargetConnectionFactory(atomikosConnectionFactoryBean);
		adapter.setUsername(getProperty("varsel.serviceuser.username"));
		adapter.setPassword(getProperty("varsel.serviceuser.password"));
		return adapter;
	}

	/**
	 * Atomikos wrapper for XAConnectionFactory
	 */
	@Bean(initMethod = "init", destroyMethod = "close")
	public ConnectionFactory atomikosConnectionFactoryBean(BrokerService brokerService) {
		AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
		atomikosConnectionFactoryBean.setUniqueResourceName(UUID.randomUUID().toString());
		atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory());
		atomikosConnectionFactoryBean.setPoolSize(10);
		return atomikosConnectionFactoryBean;
	}

	/**
	 * XA ConnectionFactory for ActiveMQ
	 */
	public static ActiveMQXAConnectionFactory activeMQXAConnectionFactory() {
		ActiveMQXAConnectionFactory factory = new ActiveMQXAConnectionFactory();
		factory.setBrokerURL(VM_LOCALHOST + "?create=false");
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(0);
		factory.setRedeliveryPolicy(redeliveryPolicy);
		return factory;
	}

	@Bean
	public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
		return new TransactionTemplate(platformTransactionManager);
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public BrokerService broker(ActiveMQQueue backoutQueue) throws URISyntaxException {
		BrokerService broker = new BrokerService();
		broker.setVmConnectorURI(new URI(VM_LOCALHOST));
		SharedDeadLetterStrategy deadLetterStrategy = new SharedDeadLetterStrategy();
		deadLetterStrategy.setDeadLetterQueue(backoutQueue);
		PolicyMap policyMap = new PolicyMap();
		PolicyEntry defaultEntry = new PolicyEntry();
		defaultEntry.setDeadLetterStrategy(deadLetterStrategy);
		policyMap.setDefaultEntry(defaultEntry);
		broker.setDestinationPolicy(policyMap);
		broker.setUseJmx(false);
		broker.setPersistent(false);
		return broker;
	}

	@Bean
	public Queue replyQueue() throws JMSException {
		return new MQQueue("reply_queue");
	}

	@Bean
	public ActiveMQQueue backoutQueue() {
		return new ActiveMQQueue("backout_queue");
	}

}
