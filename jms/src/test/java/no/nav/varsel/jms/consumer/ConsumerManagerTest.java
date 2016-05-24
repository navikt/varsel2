package no.nav.varsel.jms.consumer;

import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.jms.consumer.JmsConsumer.VARSEL_KVITTERING;
import static no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumerTest.createVarsel;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import no.nav.varsel.jms.AbstractJmsTest;
import no.nav.varsel.jms.to.JmsReply;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Queue;

/**
 * Itest for {@link ConsumerManager}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ConsumerManagerTest extends AbstractJmsTest {

	@Inject
	private Queue bestillServicemelding;

	@Before
	public void setUp() throws Exception {
		consumerManager.startAll();
		jmsTemplate.setReceiveTimeout(200);
	}

	@Test
	public void shouldStopAndStartConsumer() throws Exception {
		consumerManager.stop(BESTILL_SERVICEMELDING);

		assertFalse(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
		assertTrue(consumerManager.getListener(VARSEL_KVITTERING).isRunning());
		JmsReply jmsReply = sendMessage(bestillServicemelding, createVarsel());
		assertThat(jmsReply, nullValue());

		consumerManager.start(BESTILL_SERVICEMELDING);

		assertTrue(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
		assertTrue(consumerManager.getListener(VARSEL_KVITTERING).isRunning());
		jmsReply = sendMessage(bestillServicemelding, createVarsel());
		assertThat(jmsReply, notNullValue());
	}

	@Test
	public void shouldStopAndStartAll() throws Exception {
		consumerManager.stopAll();
		consumerManager.getListeners().forEach(l -> assertFalse(l.isRunning()));

		consumerManager.startAll();
		consumerManager.getListeners().forEach(l -> assertTrue(l.isRunning()));

	}
}