package no.nav.varsel.config;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Varselutsending;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.NamingException;

/**
 * Spring config for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableJms
@Import({QueueConfig.class})
@Configuration
public class JmsConfig {

	@Bean
	public JmsTemplate jmsTemplate(DestinationResolver destinationResolver) {
		JmsTemplate jmsTemplate = new JmsTemplate(mqConnectionFactory());
		jmsTemplate.setReceiveTimeout(10_000);
		jmsTemplate.setMessageConverter(converter());
		jmsTemplate.setConnectionFactory(mqConnectionFactory());
		jmsTemplate.setDestinationResolver(destinationResolver);
		return jmsTemplate;
	}

	@Bean
	public BeanFactoryDestinationResolver destinationResolver(BeanFactory beanFactory) {
		return new BeanFactoryDestinationResolver(beanFactory);
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(DestinationResolver destinationResolver,
																		  MessageConverter converter) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(mqConnectionFactory());
		factory.setDestinationResolver(destinationResolver);
		factory.setMessageConverter(new ConsumerMessageConverter(converter));
		return factory;
	}

	@Bean
	public MessageConverter converter() {
		MarshallingMessageConverter converter = new MarshallingMessageConverter(marshaller());
		converter.setTargetType(MessageType.TEXT);
		return converter;
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(
				Varsel.class.getPackage().getName(),
				Varselutsending.class.getPackage().getName(),
				VarselKvittering.class.getPackage().getName(),
				JmsReply.class.getPackage().getName()
		);
		return marshaller;
	}

	@Bean
	public ConnectionFactory mqConnectionFactory() {
		ConnectionFactory connectionFactory = getJndiObject("java:/jboss/mqConnectionFactory", ConnectionFactory.class);
		UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
		adapter.setTargetConnectionFactory(connectionFactory);
		adapter.setUsername("srvappserver");
		adapter.setPassword("");
		return adapter;
	}

	/**
	 * Only convert to message, not from Message, used for replies in jms
	 */
	private static class ConsumerMessageConverter implements MessageConverter {
		private final MessageConverter replyConverter;

		ConsumerMessageConverter(MessageConverter replyConverter) {
			this.replyConverter = replyConverter;
		}

		@Override
		public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
			return replyConverter.toMessage(object, session);
		}

		@Override
		public Object fromMessage(Message message) throws JMSException, MessageConversionException {
			return message;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getJndiObject(String jndiName, Class<T> expectedType) {
		JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
		factory.setJndiName(jndiName);
		factory.setExpectedType(expectedType);
		try {
			factory.afterPropertiesSet();
		} catch (IllegalArgumentException | NamingException e) {
			throw new RuntimeException(e);
		}
		return (T) factory.getObject();
	}
}
