package no.nav.varsel.config;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.extern.slf4j.Slf4j;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.varsel.config.alias.ListenerProperties;
import no.nav.varsel.config.alias.MqGatewayProperties;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.net.ssl.SSLSocketFactory;

import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_CHARACTER_SET;
import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_ENCODING;
import static com.ibm.msg.client.jakarta.wmq.common.CommonConstants.WMQ_CM_CLIENT;
import static com.ibm.msg.client.jakarta.wmq.compat.base.internal.MQC.MQENC_NATIVE;

/**
 * Spring config for JMS
 */
@EnableJms
@Import({QueueConfig.class})
@Configuration
@Slf4j
public class JmsConfig {

	@Value("${varsel.jms.consumer.min}")
	private Integer minimumConsumers;
	@Value("${varsel.jms.consumer.max}")
	private Integer maximumConsumers;

	@Value("${varsel.serviceuser.username}")
	private String srvVarselUsername;
	@Value("${varsel.serviceuser.password}")
	private String srvVarselPassword;

	private static final Logger LOG = LoggerFactory.getLogger(JmsConfig.class);
	private static final String ANY_TLS13_OR_HIGHER = "*TLS13ORHIGHER";
	private static final int UTF_8_WITH_PUA = 1208;


	@Bean
	public JmsTemplate jmsTemplate(DestinationResolver destinationResolver,
								   ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setReceiveTimeout(5_000);
		jmsTemplate.setMessageConverter(converter());
		jmsTemplate.setConnectionFactory(connectionFactory);
		jmsTemplate.setDestinationResolver(destinationResolver);
		return jmsTemplate;
	}

	@Bean
	public BeanFactoryDestinationResolver destinationResolver(BeanFactory beanFactory) {
		return new BeanFactoryDestinationResolver(beanFactory);
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(DestinationResolver destinationResolver,
																		  ConnectionFactory connectionFactory,
																		  PlatformTransactionManager transactionManager,
																		  final ListenerProperties listenerProperties) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setDestinationResolver(destinationResolver);
		factory.setMessageConverter(new ConsumerMessageConverter(converter()));
		factory.setTransactionManager(transactionManager);
		factory.setConcurrency(String.format("%d-%d", minimumConsumers, maximumConsumers));
		factory.setAutoStartup(listenerProperties.isAutoStartup());
		log.info("Listener autostart: " + listenerProperties.isAutoStartup());
		factory.setErrorHandler(t -> {
			Throwable throwable = t;
			if (t instanceof ListenerExecutionFailedException) {
				throwable = t.getCause();
			}
			LOG.error("Execution of JMS message failed", throwable);
		});
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
				XMLVarsel.class.getPackage().getName(),
				JmsReply.class.getPackage().getName()
		);
		return marshaller;
	}

	@Bean
	public ConnectionFactory connectionFactory(final MqGatewayProperties mqGatewayAlias) throws JMSException {
		MQConnectionFactory connectionFactory = new MQConnectionFactory();
		connectionFactory.setHostName(mqGatewayAlias.getHostname());
		connectionFactory.setPort(mqGatewayAlias.getPort());
		connectionFactory.setQueueManager(mqGatewayAlias.getName());
		connectionFactory.setChannel(mqGatewayAlias.getChannel().getSecurename());
		connectionFactory.setTransportType(WMQ_CM_CLIENT);
		connectionFactory.setCCSID(UTF_8_WITH_PUA);
		connectionFactory.setIntProperty(JMS_IBM_ENCODING, MQENC_NATIVE);
		connectionFactory.setIntProperty(JMS_IBM_CHARACTER_SET, UTF_8_WITH_PUA);

		connectionFactory.setSSLCipherSuite(ANY_TLS13_OR_HIGHER);
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		connectionFactory.setSSLSocketFactory(factory);

		UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
		adapter.setTargetConnectionFactory(connectionFactory);
		adapter.setUsername(srvVarselUsername);
		adapter.setPassword(srvVarselPassword);

		JmsPoolConnectionFactory pooledFactory = new JmsPoolConnectionFactory();
		pooledFactory.setConnectionFactory(adapter);
		pooledFactory.setMaxConnections(10);
		pooledFactory.setMaxSessionsPerConnection(10);
		return pooledFactory;
	}

	/**
	 * Only convert to Message, not from Message, used for replies in jms
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
		public Object fromMessage(Message message) throws MessageConversionException {
			return message;
		}
	}

}
