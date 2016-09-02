package no.nav.varsel.jms.consumer;

import org.springframework.util.Assert;

import javax.jms.Message;

/**
 * Wrapper that holds the message and the unmarshalled object of the message
 * @author Lars Aune
 */
public class ObjectMessageWrapper<T> {

	private T object;
	private Message message;

	public ObjectMessageWrapper(T t, Message message) {
		Assert.notNull(t, "Unmarshalled object can't be null");
		Assert.notNull(message, "Message can't be null");
		this.object = t;
		this.message = message;
	}

	public T getObject() {
		return object;
	}

	public Message getMessage() {
		return message;
	}
}
