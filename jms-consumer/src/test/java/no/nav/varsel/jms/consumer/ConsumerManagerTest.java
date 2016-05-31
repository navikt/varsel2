package no.nav.varsel.jms.consumer;

import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.jms.consumer.JmsConsumer.VARSEL_KVITTERING;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Itest for {@link ConsumerManager}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ConsumerManagerTest extends AbstractConsumerJmsTest {

	@Inject
	protected ConsumerManager consumerManager;

	@Before
	public void setUp() throws Exception {
		consumerManager.startAll();
		jmsTemplate.setReceiveTimeout(500);
	}

	@Test
	public void shouldStopAndStartConsumer() throws Exception {
		consumerManager.stop(BESTILL_SERVICEMELDING);

		assertFalse(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
		assertTrue(consumerManager.getListener(VARSEL_KVITTERING).isRunning());

		consumerManager.start(BESTILL_SERVICEMELDING);

		assertTrue(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
		assertTrue(consumerManager.getListener(VARSEL_KVITTERING).isRunning());
	}

	@Test
	public void shouldStopAndStartAll() throws Exception {
		consumerManager.stopAll();
		consumerManager.getListeners().forEach(l -> assertFalse(l.isRunning()));

		consumerManager.startAll();
		consumerManager.getListeners().forEach(l -> assertTrue(l.isRunning()));

	}
}