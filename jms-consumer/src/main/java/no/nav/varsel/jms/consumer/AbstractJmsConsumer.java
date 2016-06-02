package no.nav.varsel.jms.consumer;

import no.nav.varsel.domain.Constants;
import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * Helper class for JMS consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public abstract class AbstractJmsConsumer<T> {

	private static final Logger NO_BACKOUTLOG = LoggerFactory.getLogger("no.nav.varsel.jms.nobackoutlog");

	private final JmsConsumer jmsConsumer;
	private final Class<T> inputType;
	@Inject
	private Jaxb2Marshaller marshaller;
	@Inject
	private JmsConsumerManager jmsConsumerManager;

	public AbstractJmsConsumer(JmsConsumer jmsConsumer, Class<T> inputType) {
		this.jmsConsumer = jmsConsumer;
		this.inputType = inputType;
	}

	protected abstract void handleMessage(T message);

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
		unmarshalAndHandle(message);
		return reply(message);
	}

	private T unmarshalAndHandle(TextMessage message) {
		T unmarshalledObject = null;
		try {
			unmarshalledObject = unmarshal(message);
			handleMessage(unmarshalledObject);
		} catch (NoJmsBackoutException e) {
			NO_BACKOUTLOG.warn("Nonbackout " + errorFor(message), e);
		} catch (Exception e) {
			jmsConsumerManager.registerError(jmsConsumer);
			throw new RuntimeException(errorFor(message), e);
		}
		return unmarshalledObject;
	}

	private String errorFor(TextMessage message) {
		return String.format("Error in service=%s during processing of message: %s",
				jmsConsumer.getServiceName(), messageToString(message));
	}

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

	private String messageToString(TextMessage message) {
		try {
			return message.getText();
		} catch (JMSException e) {
			return e.getMessage();
		}
	}

	private JmsReply reply(Message message) {
		try {
			Destination replyTo = message.getJMSReplyTo();
			if (replyTo != null) {
				return new JmsReply("ok");
			}
		} catch (JMSException ignore) {
			// implementations do not throw exception
		}
		return null;
	}
}
