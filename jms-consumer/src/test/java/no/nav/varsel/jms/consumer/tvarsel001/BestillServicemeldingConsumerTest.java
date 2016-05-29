package no.nav.varsel.jms.consumer.tvarsel001;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;

/**
 * Itest for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	@Inject
	private Queue bestillServicemeldingQueue;

	@Test
	public void shouldReceieveJms() throws Exception {
		JmsReply response = sendMessage(bestillServicemeldingQueue, createVarsel());
//		new JmsConfig().marshaller().marshal(createVarsel(), new StreamResult(System.out));

		assertTrue(response != null && response.isOk());
		assertThat(varselbestillingRepo.findAll(), hasSize(1));
	}

	@Test
	public void shouldPutOnBackoutIfFailedWs() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		varsel.getValue().getVarslingstype().setValue("feil");
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		assertThat(response, notNullValue());
	}

	@Test
	public void shouldPutOnBackoutAndRollbackIfFailedAfterDbSave() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		varsel.getValue().getVarslingstype().setValue("feilMqUt");
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		assertThat(response, notNullValue());
		assertThat(varselbestillingRepo.findAll(), hasSize(0));
	}

	public static JAXBElement<Varsel> createVarsel() {
		return new ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
	}
}