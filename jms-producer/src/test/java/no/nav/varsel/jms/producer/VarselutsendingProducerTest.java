package no.nav.varsel.jms.producer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.ObjectFactory;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.jms.producer.varselutsending.support.VarselutsendingMapper;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Unit test for VarselutsendingProducer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class VarselutsendingProducerTest {

	@Mock
	private VarselutsendingMapper varselutsendingMapperMock;
	@Mock
	private JmsTemplate jmsTemplateMock;
	@Mock
	private Queue varselutsendingQueueMock;
	@Mock
	private ObjectFactory objectFactory;

	@InjectMocks
	private VarselutsendingProducer producer;

	private VarselutsendingTo varselutsendingTo;
	private JAXBElement<Varselutsending> jaxbElement;

	@Before
	public void setUp() throws Exception {
		varselutsendingTo = new VarselutsendingTo();
		Varselutsending varselutsending = new Varselutsending();
		when(varselutsendingMapperMock.map(varselutsendingTo)).thenReturn(varselutsending);
		jaxbElement = new JAXBElement<>(new QName(""), Varselutsending.class, varselutsending);
		when(objectFactory.createVarselutsending(varselutsending)).thenReturn(jaxbElement);
	}

	@Test
	public void shouldProduce() throws Exception {
		producer.produce(varselutsendingTo);

		verify(jmsTemplateMock).convertAndSend(varselutsendingQueueMock, jaxbElement);
	}
}