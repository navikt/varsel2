package no.nav.varsel.jms.consumer.tvarsel001;

import static org.junit.Assert.assertTrue;

import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varslingstyper;
import no.nav.varsel.jms.AbstractJmsTest;
import no.nav.varsel.jms.to.JmsReply;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.util.HashMap;

/**
 * Itste for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingConsumerTest extends AbstractJmsTest {

	@Inject
	private Queue bestillServicemelding;

	private BestillServicemeldingConsumer consumer = new BestillServicemeldingConsumer();

	@Test
	public void shouldAcceptJAXBElement() throws Exception {
		JAXBElement<Varsel> varselJAXBElement = createVarsel();
		consumer.bestillServicemelding(varselJAXBElement, new HashMap<>());
//		new JmsConfig().marshaller().marshal(varselJAXBElement, new StreamResult(System.out));
	}

	@Test
	public void shouldReceieveJms() throws Exception {
		JmsReply response = sendMessage(bestillServicemelding, createVarsel());

		assertTrue(response.isOk());
	}

	public static JAXBElement<Varsel> createVarsel() {
		Varsel varsel = new Varsel();
		Varslingstyper varslingstype = new Varslingstyper();
		varslingstype.setValue("hei");
		varsel.setVarslingstype(varslingstype);
		return new ObjectFactory().createVarsel(varsel);
	}
}