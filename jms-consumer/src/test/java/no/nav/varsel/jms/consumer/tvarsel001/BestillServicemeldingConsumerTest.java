package no.nav.varsel.jms.consumer.tvarsel001;

import static org.junit.Assert.assertTrue;

import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;

/**
 * Itest for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	@Inject
	private Queue bestillServicemelding;

	@Inject
	private BestillServicemeldingConsumer consumer;

	//		new JmsConfig().marshaller().marshal(varselJAXBElement, new StreamResult(System.out));

	@Test
	public void shouldReceieveJms() throws Exception {
		JmsReply response = sendMessage(bestillServicemelding, createVarsel());

		assertTrue(response.isOk());
	}

	public static JAXBElement<Varsel> createVarsel() {
		return new ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
	}
}