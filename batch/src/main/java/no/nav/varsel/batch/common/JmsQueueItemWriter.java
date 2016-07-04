package no.nav.varsel.batch.common;

import org.springframework.batch.item.ItemWriter;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;
import javax.jms.Queue;
import java.util.List;
import java.util.function.Function;

/**
 * Use shared jmstemplate to send to a queue and optionally apply a mapping first
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class JmsQueueItemWriter<T> implements ItemWriter<T> {

	private JmsTemplate jmsTemplate;
	private Queue destination;
	private Function<T, ?> mapper;

	@Override
	public void write(List<? extends T> items) throws Exception {
		for (T item : items) {
			Object result = map(item);
			jmsTemplate.convertAndSend(destination, result);
		}
	}

	private Object map(T item) {
		if (mapper != null) {
			return mapper.apply(item);
		} else {
			return item;
		}
	}

	@Inject
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDestination(Queue destination) {
		this.destination = destination;
	}

	public void setMapper(Function<T, ?> mapper) {
		this.mapper = mapper;
	}
}
