package no.nav.varsel.config;

import static no.nav.varsel.config.QueueConfig.getQueue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
@EnableAutoConfiguration
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
		ctx.bind("java:/jboss/mqConnectionFactory", mqConnectionFactory());

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

	private static ActiveMQConnectionFactory mqConnectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(VM_LOCALHOST + "?create=false");
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(0);
		factory.setRedeliveryPolicy(redeliveryPolicy);
		return factory;
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
