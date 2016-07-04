package no.nav.varsel.batch.common;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;
import java.util.function.Function;

/**
 * Unit test for {@link JmsQueueItemWriter}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class JmsQueueItemWriterTest {

	@Mock
	private JmsTemplate jmsTemplate;
	@Mock
	private Queue destination;
	@Mock
	private Function<Object, Object> mapper;

	@InjectMocks
	private JmsQueueItemWriter<Object> writer;

	@Test
	public void shouldMapAndQueue() throws Exception {
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();
		when(mapper.apply(o1)).thenReturn(o3);
		when(mapper.apply(o2)).thenReturn(o4);

		writer.write(Lists.newArrayList(o1, o2));

		verify(jmsTemplate).convertAndSend(destination, o3);
		verify(jmsTemplate).convertAndSend(destination, o4);
		verifyNoMoreInteractions(jmsTemplate);
	}

	@Test
	public void shouldQueueIfNoMapperPresent() throws Exception {
		writer.setMapper(null);
		Object o1 = new Object();
		Object o2 = new Object();

		writer.write(Lists.newArrayList(o1, o2));

		verify(jmsTemplate).convertAndSend(destination, o1);
		verify(jmsTemplate).convertAndSend(destination, o2);
		verifyNoMoreInteractions(jmsTemplate, mapper);
	}
}