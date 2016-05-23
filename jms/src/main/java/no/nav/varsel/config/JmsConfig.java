package no.nav.varsel.config;

import static no.nav.varsel.ServiceConfig.getJndiObject;

import no.nav.varsel.ServiceConfig;
import no.nav.varsel.jms.consumer.config.ConsumerConfig;
import no.nav.varsel.jms.producer.config.ProducerConfig;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/**
 * Spring config for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableJms
@Import({ServiceConfig.class, ProducerConfig.class, ConsumerConfig.class})
@Configuration
public class JmsConfig {

	@Bean
	public BeanFactoryDestinationResolver destinationResolver(BeanFactory beanFactory) {
		return new BeanFactoryDestinationResolver(beanFactory);
	}

	@Bean
	public JmsListenerContainerFactory jmsListenerContainerFactory(DestinationResolver destinationResolver) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(mqConnectionFactory());
		factory.setDestinationResolver(destinationResolver);
		return factory;
	}

	private UserCredentialsConnectionFactoryAdapter mqConnectionFactory() {
		ConnectionFactory connectionFactory = getJndiObject("java:/jboss/mqConnectionFactory", ConnectionFactory.class);
		UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
		adapter.setTargetConnectionFactory(connectionFactory);
		adapter.setUsername("srvappserver");
		adapter.setPassword("");
		return adapter;
	}

	@Bean
	public Queue bestillServiceMelding() {
		return getJndiObject("java:/jboss/bestillServicemelding", Queue.class);
	}

	@Bean
	public Queue varselKvittering() {
		return getJndiObject("java:/jboss/varselKvittering", Queue.class);
	}
}
