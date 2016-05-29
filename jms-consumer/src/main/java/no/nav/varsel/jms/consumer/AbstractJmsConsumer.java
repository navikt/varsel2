package no.nav.varsel.jms.consumer;

import no.nav.varsel.jms.to.xml.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
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
public abstract class AbstractJmsConsumer {

	private static final Logger LOGG = LoggerFactory.getLogger(AbstractJmsConsumer.class);

	@Inject
	private JmsTemplate jmsTemplate;
	@Inject
	private Jaxb2Marshaller marshaller;

	@SuppressWarnings("unchecked")
	protected <T> T unmarshal(TextMessage message, Class<T> clazz) {
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
			if (clazz.isAssignableFrom(object.getClass())) {
				return (T) object;
			}
			throw new RuntimeException("Object is not expected class: " + clazz.getName() + " found " + object.getClass());
		} catch (Exception e) {
			throw new RuntimeException("Invalid message, cannot be unmarshalled: " + messageToString(message), e);
		}
	}

	private String messageToString(TextMessage message) {
		try {
			return message.getText();
		} catch (JMSException e) {
			return e.getMessage();
		}
	}

	protected JmsReply reply(Message message) {
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
