package no.nav.varsel.config;

import static no.nav.varsel.config.QueueConfig.getQueue;

import com.atomikos.jms.AtomikosConnectionFactoryBean;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.command.ActiveMQQueue;
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

/**
 * Test Config for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableAutoConfiguration(exclude = {DataSourceTransactionManagerAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import({JmsConfig.class})
@Configuration
public class JmsTestConfig {

	private static final String VM_LOCALHOST = "vm://localhost";

	public static void mockJndi() throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		InitialContext ctx = new InitialContext();
		try {
			ctx.destroySubcontext("java:");
		} catch (NamingException e) {
			// ignore
		}

		ctx.createSubcontext("java:");
		ctx.createSubcontext("java:/jboss");

		// Queue mocks
		ctx.bind("java:/jboss/bestillServicemelding", new ActiveMQQueue("bestillServicemelding"));
		ctx.bind("java:/jboss/varselKvittering", new ActiveMQQueue("varselKvittering"));
		ctx.bind("java:/jboss/varselutsending", new ActiveMQQueue("varselutsending"));
		ctx.bind("java:/jboss/bestillVarsel", new ActiveMQQueue("bestillVarsel"));
		ctx.bind("java:/jboss/revarselStopp", new ActiveMQQueue("revarselStopp"));

		// Test queues
		ctx.bind("java:/jboss/backout", new ActiveMQQueue("backout"));
		ctx.bind("java:/jboss/reply", new ActiveMQQueue("reply"));
	}

	/**
	 * Using the same username/password-wrapper as in production to ensure it delegates down to XAConnectionFactory
	 */
	@Bean
	public ConnectionFactory connectionFactory(ConnectionFactory atomikosConnectionFactoryBean) throws JMSException {
		UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
		adapter.setTargetConnectionFactory(atomikosConnectionFactoryBean);
		adapter.setUsername("srvappserver");
		adapter.setPassword("");
		return adapter;
	}

	/**
	 * Atomikos wrapper for XAConnectionFactory
	 */
	@Bean(initMethod = "init", destroyMethod = "close")
	public ConnectionFactory atomikosConnectionFactoryBean() {
		AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
		atomikosConnectionFactoryBean.setUniqueResourceName("QUEUE_BROKER");
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
	public Queue replyQueue() {
		return getQueue("java:/jboss/reply");
	}

	@Bean
	public ActiveMQQueue backoutQueue() {
		return (ActiveMQQueue) getQueue("java:/jboss/backout");
	}

}
