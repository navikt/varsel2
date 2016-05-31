package no.nav.varsel.jms.consumer;

import static java.lang.Thread.sleep;
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

	private static final int RESTART_TIME_SECONDS = 1;
	private static final int CONTEXT_TIME_SECONDS = 1;

	@Inject
	protected ConsumerManager consumerManager;

	@Before
	public void setUp() throws Exception {
		consumerManager.startAll();
		consumerManager.setContextSize(3);
		consumerManager.setContextTimeSeconds(CONTEXT_TIME_SECONDS);
		consumerManager.setRestartTimeSeconds(RESTART_TIME_SECONDS);
		jmsTemplate.setReceiveTimeout(500);

		consumerManager.clearErrors();
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

	@Test
	public void shouldNotStopIfNotTooManyErrors() throws Exception {
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		assertTrue(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
	}

	@Test
	public void shouldNotStopIfErrorsTooSpreadOut() throws Exception {
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		sleep(CONTEXT_TIME_SECONDS * 2 * 1000);
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		assertTrue(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
	}

	@Test
	public void shouldStopQueueIfTooManyErrors() throws Exception {
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		consumerManager.registerError(BESTILL_SERVICEMELDING);
		assertFalse(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());

		sleep(RESTART_TIME_SECONDS * 1000 / 2);
		assertFalse(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());

		// Should start again
		sleep(RESTART_TIME_SECONDS * 1000);
		assertTrue(consumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());

	}
}