package no.nav.varsel.jms.consumer.config;

import static no.nav.varsel.ServiceConfig.getJndiObject;

import no.nav.varsel.config.JmsConfig;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Test Config for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableAutoConfiguration
@Import(JmsConfig.class)
@Configuration
public class JmsTestConfig {

	public JmsTestConfig() throws NamingException {
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
		ctx.bind("java:/jboss/mqConnectionFactory", new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false"));
		ctx.bind("java:/jboss/bestillServicemelding", new ActiveMQQueue("bestillServicemelding"));
		ctx.bind("java:/jboss/varselKvittering", new ActiveMQQueue("varselKvittering"));
		ctx.bind("java:/jboss/reply", new ActiveMQQueue("reply"));
	}

	@Bean
	public Queue reply() {
		return getJndiObject("java:/jboss/reply", Queue.class);
	}

}
