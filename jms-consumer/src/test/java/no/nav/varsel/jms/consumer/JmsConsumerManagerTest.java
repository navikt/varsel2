package no.nav.varsel.jms.consumer;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.Thread.sleep;
import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.jms.consumer.JmsConsumer.VARSEL_KVITTERING;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Denne testen manipulerer JmsTemplate og JmsConsumerManager for de ørvrige testene.
public class JmsConsumerManagerTest extends AbstractConsumerJmsTest {

	private static final int RESTART_TIME_SECONDS = 1;
	private static final int CONTEXT_TIME_SECONDS = 1;

	@Autowired
	protected JmsConsumerManager jmsConsumerManager;

	@Before
	public void setUp() throws Exception {
		jmsConsumerManager.startAll();
		jmsConsumerManager.setContextSize(3);
		jmsConsumerManager.setContextTimeSeconds(CONTEXT_TIME_SECONDS);
		jmsConsumerManager.setRestartTimeSeconds(RESTART_TIME_SECONDS);
		jmsTemplate.setReceiveTimeout(500);

		jmsConsumerManager.clearErrors();
	}

	@Test
	public void shouldStopAndStartConsumer() {
		jmsConsumerManager.stop(BESTILL_SERVICEMELDING);

		assertFalse(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
		assertTrue(jmsConsumerManager.getListener(VARSEL_KVITTERING).isRunning());

		jmsConsumerManager.start(BESTILL_SERVICEMELDING);

		assertTrue(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
		assertTrue(jmsConsumerManager.getListener(VARSEL_KVITTERING).isRunning());
	}

	@Test
	public void shouldStopAndStartAll() {
		jmsConsumerManager.stopAll();
		jmsConsumerManager.getListeners().forEach(l -> assertFalse(l.isRunning()));

		jmsConsumerManager.startAll();
		jmsConsumerManager.getListeners().forEach(l -> assertTrue(l.isRunning()));
	}

	@Test
	public void shouldNotStopIfNotTooManyErrors() {
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		assertTrue(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
	}

	@Test
	public void shouldNotStopIfErrorsTooSpreadOut() throws Exception {
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		sleep(CONTEXT_TIME_SECONDS * 2 * 1000);
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		assertTrue(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
	}

	@Test
	public void shouldStopQueueIfTooManyErrors() throws Exception {
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		jmsConsumerManager.registerError(BESTILL_SERVICEMELDING);
		assertFalse(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());

		sleep(RESTART_TIME_SECONDS * 1000 / 2);
		assertFalse(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());

		// Should start again
		sleep(RESTART_TIME_SECONDS * 1000);
		assertTrue(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());

	}
}