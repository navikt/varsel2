package no.nav.varsel.jms.consumer;

import no.nav.varsel.domain.Constants;
import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public abstract class AbstractJmsConsumer<T> implements InitializingBean {

	public static final String JMS_NOBACKOUTLOG = "no.nav.varsel.jms.nobackoutlog";
	private static final Logger NO_BACKOUTLOG = LoggerFactory.getLogger(JMS_NOBACKOUTLOG);
	private static final Logger LOG = LoggerFactory.getLogger(AbstractJmsConsumer.class);

	private final JmsConsumer jmsConsumer;
	protected final JmsTemplate jmsSend;
	private final Class<T> inputType;

	private Jaxb2Marshaller marshaller;
	private JmsConsumerManager jmsConsumerManager;

	public AbstractJmsConsumer(JmsConsumer jmsConsumer, JmsTemplate jmsSend, Class<T> inputType) {
		this.jmsConsumer = jmsConsumer;
		this.jmsSend = jmsSend;
		this.inputType = inputType;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(marshaller, "marshaller cannot be null");
		Assert.notNull(jmsConsumerManager, "jmsConsumerManager cannot be null");
	}

	/**
	 * Method that handles a unmarshalled message. It is also possible to process header parameters, as the objectMessageWrapper
	 * wraps the unmarshalled message with the original message.
	 *
	 * @param objectMessageWrapper The unmarshalled message and the original message wrapped in an ObjectMessageWrapper
	 */
	protected abstract void handleMessage(ObjectMessageWrapper<T> objectMessageWrapper);

	/**
	 * Listener annotate with @{@link JmsListener}.
	 * destination must be spring bean name of the queue to listen to.
	 * id should be from {@link JmsConsumer.ConsumerNames}.
	 * <p>
	 * Method should call {@link AbstractJmsConsumer#doListen(TextMessage)}
	 *
	 * @param message the message to be received
	 * @return JmsReply if a replyto was specified
	 */
	public abstract JmsReply listen(TextMessage message);

	protected JmsReply doListen(TextMessage message) {
		MDC.put(Constants.USER_ID, jmsConsumer.getServiceName());
		try {
			unmarshalAndHandle(message);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			// This will never happen as unmarshalAndHandle doesnt throw checked exceptions
			throw new RuntimeException(e);
		}
		return reply(message);
	}

	private T unmarshalAndHandle(TextMessage message) {
		T unmarshalledObject = null;
		try {
			unmarshalledObject = unmarshal(message);
			handleMessage(new ObjectMessageWrapper<>(unmarshalledObject, message));
		} catch (NoJmsBackoutException e) {
			handleNoJmsBackout(e, message);
		} catch (Exception e) {
			jmsConsumerManager.registerError(jmsConsumer);
			throw new RuntimeException("Error in service=" + jmsConsumer.getServiceName(), e);
		}
		return unmarshalledObject;
	}

	private void handleNoJmsBackout(NoJmsBackoutException originalError, TextMessage message) {
		NO_BACKOUTLOG.warn("Nonbackout Error in service={}. Writing to functional error queue", jmsConsumer.getServiceName(), originalError);
		try {
			performWriteToFunctionalErrorQueue(message);
		} catch (Exception e) {
			NO_BACKOUTLOG.error("Unable to write message to functional error queue in service={}. Message discarded.", jmsConsumer.getServiceName(), e);
		}
	}

	protected abstract void performWriteToFunctionalErrorQueue(TextMessage message);

	@SuppressWarnings("unchecked")
	private T unmarshal(TextMessage message) {
		try {
			String text = message.getText();
			Source source = new StreamSource(new StringReader(text));
			Object unmarshal = marshaller.unmarshal(source);
			Object object;
			if (unmarshal instanceof JAXBElement) {
				object = ((JAXBElement) unmarshal).getValue();
			} else {
				object = unmarshal;
			}
			if (inputType.isAssignableFrom(object.getClass())) {
				return (T) object;
			} else {
				throw new RuntimeException("Object is not expected class: " + inputType.getName() + " found " + object.getClass());
			}
		} catch (Exception e) {
			throw new NoJmsBackoutException("Invalid message, cannot be unmarshalled", e);
		}
	}

	private JmsReply reply(Message message) {
		try {
			Destination replyTo = message.getJMSReplyTo();
			if (replyTo != null) {
				return new JmsReply("ok");
			}
		} catch (JMSException e) {
			// implementations do not throw exception
			LOG.trace("cannot create jms reply", e);
		}
		return null;
	}

	@Autowired
	public void setMarshaller(Jaxb2Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	@Autowired
	public void setJmsConsumerManager(JmsConsumerManager jmsConsumerManager) {
		this.jmsConsumerManager = jmsConsumerManager;
	}
}
