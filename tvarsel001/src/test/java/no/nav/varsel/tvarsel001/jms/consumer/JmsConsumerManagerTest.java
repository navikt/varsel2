package no.nav.varsel.tvarsel001.jms.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static java.lang.Thread.sleep;
import static no.nav.varsel.tvarsel001.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS) // Denne testen manipulerer JmsTemplate og JmsConsumerManager for de øvrige testene.
public class JmsConsumerManagerTest extends AbstractConsumerJmsTest {

	private static final int RESTART_TIME_SECONDS = 1;
	private static final int CONTEXT_TIME_SECONDS = 1;

	@Autowired
	protected JmsConsumerManager jmsConsumerManager;


	@BeforeEach
	public void setUp() {
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

		jmsConsumerManager.start(BESTILL_SERVICEMELDING);
		assertTrue(jmsConsumerManager.getListener(BESTILL_SERVICEMELDING).isRunning());
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