package no.nav.varsel.jms.consumer;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import no.nav.varsel.domain.Constants;
import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;

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
public abstract class AbstractJmsConsumer<T> implements InitializingBean {

	private static final Logger NO_BACKOUTLOG = LoggerFactory.getLogger("no.nav.varsel.jms.nobackoutlog");
	private static final Logger LOG = LoggerFactory.getLogger(AbstractJmsConsumer.class);

	private final JmsConsumer jmsConsumer;
	private final Class<T> inputType;

	private Jaxb2Marshaller marshaller;
	private JmsConsumerManager jmsConsumerManager;

	private MetricRegistry metricRegistry;
	private Timer timer;
	private Meter exceptionMeter;
	private Meter noBackoutExceptionMeter;

	public AbstractJmsConsumer(JmsConsumer jmsConsumer, Class<T> inputType) {
		this.jmsConsumer = jmsConsumer;
		this.inputType = inputType;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String metricBase = String.format("varsel.%s.%s", jmsConsumer.getServiceName(), jmsConsumer.getConsumerName());
		timer = metricRegistry.timer(metricBase + ".timer");
		exceptionMeter = metricRegistry.meter(metricBase + ".excpetionMeter");
		noBackoutExceptionMeter = metricRegistry.meter(metricBase + ".noBackoutExceptionMeter");

		Assert.notNull(marshaller, "marshaller cannot be null");
		Assert.notNull(jmsConsumerManager, "jmsConsumerManager cannot be null");
		Assert.notNull(metricRegistry, "metricRegistry cannot be null");
	}

	/**
	 * Method that handles a message
	 *
	 * @param message message unmarshalled in the type of this consumer
	 */
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
		try {
			timer.time(() -> unmarshalAndHandle(message));
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
			handleMessage(unmarshalledObject);
		} catch (NoJmsBackoutException e) {
			noBackoutExceptionMeter.mark();
			NO_BACKOUTLOG.warn("Nonbackout " + errorFor(message), e);
		} catch (Exception e) {
			exceptionMeter.mark();
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
		} catch (JMSException e) {
			// implementations do not throw exception
			LOG.trace("cannot create jms reply", e);
		}
		return null;
	}

	@Inject
	public void setMarshaller(Jaxb2Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	@Inject
	public void setJmsConsumerManager(JmsConsumerManager jmsConsumerManager) {
		this.jmsConsumerManager = jmsConsumerManager;
	}

	@Inject
	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}
}
