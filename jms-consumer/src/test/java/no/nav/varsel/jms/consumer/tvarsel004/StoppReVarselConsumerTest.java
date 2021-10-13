package no.nav.varsel.jms.consumer.tvarsel004;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.ObjectFactory;
import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.StoppReVarsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.JmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.consumer.tvarsel004.support.StoppReVarselMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Itest for {@link StoppReVarselConsumer}
 *
 * @author Hiep Luong Nguyen, Computas
 */
public class StoppReVarselConsumerTest extends AbstractConsumerJmsTest {

	private static final String VARSELBESTILLING_ID = "VARSELBESTILLING_ID";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Inject
	private Queue revarselStoppQueue;
	@Inject
	private Queue bestillServicemeldingQueue;

	@Test
	public void shouldPersistStoppReVarselMessage() throws Exception {
		Varselbestilling varselbestilling = persistVarselbestilling();
		JAXBElement<StoppReVarsel> stoppReVarsel = createStoppReVarselJaxBElement(varselbestilling.getVarselbestillingId());
		JmsReply response = sendMessage(revarselStoppQueue, stoppReVarsel);
		assertThat(response.isOk(), is(true));

		Varselbestilling processedVarselbestilling = varselbestillingRepo.findByVarselbestillingId(varselbestilling.getVarselbestillingId());
		assertThat(processedVarselbestilling.getAntallRevarslinger(), equalTo(0));
		assertThat(processedVarselbestilling.getNesteVarslingDato(), is(nullValue()));
		assertThat(processedVarselbestilling.getChangeStamp().getEndretAv(), is(JmsConsumer.REVARSEL_STOPP.getServiceName()));
	}

	@Test
	public void logsMelding() {
		Varselbestilling varselbestilling = persistVarselbestilling();
		varselbestilling.setVarselbestillingId(VARSELBESTILLING_ID);
		JAXBElement<StoppReVarsel> stoppReVarsel = createStoppReVarselJaxBElement(varselbestilling.getVarselbestillingId());
		Appender<ILoggingEvent> loggerAppender = getMockedAppender();

		sendMessage(revarselStoppQueue, stoppReVarsel);

		verify(loggerAppender, times(1)).doAppend(argThat(hasMessageContaining("Mottatt kall for å stoppe revarsel for varselbestillingId=VARSELBESTILLING_ID")));
	}

	public static JAXBElement<StoppReVarsel> createStoppReVarselJaxBElement(String varselbestillingId) {
		return new ObjectFactory().createStoppReVarsel(createStoppReVarsel(varselbestillingId));
	}

	private static StoppReVarsel createStoppReVarsel(String varselbestillingId) {
		StoppReVarsel stoppReVarsel = StoppReVarselMapperTest.createStoppReVarsel();
		stoppReVarsel.setVarselbestillingId(varselbestillingId);

		return stoppReVarsel;
	}

	private Varselbestilling persistVarselbestilling() {
		JmsReply createVarselresponse = sendMessage(bestillServicemeldingQueue, createVarsel());
		assertTrue(createVarselresponse != null && createVarselresponse.isOk());
		List<Varselbestilling> varselbestillings = varselbestillingRepo.findAllEager();
		assertThat(varselbestillings, hasSize(1));
		Varselbestilling varselbestilling = varselbestillings.iterator().next();
		assertThat(varselbestilling.getVarsels(), hasSize(1));
		varselbestilling.setAntallRevarslinger(5);
		varselbestilling.setNesteVarslingDato(LocalDate.now());
		return varselbestilling;
	}

	public static JAXBElement<Varsel> createVarsel() {
		return new no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
	}

	private Appender<ILoggingEvent> getMockedAppender() {
		Logger testLogger = (Logger) LoggerFactory.getLogger(StoppReVarselConsumer.class);
		Appender<ILoggingEvent> mockAppender = Mockito.mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		testLogger.addAppender(mockAppender);

		return mockAppender;
	}

	private ArgumentMatcher<ILoggingEvent> hasMessageContaining(final String token) {
		return new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final Object argument) {
				return ((LoggingEvent) argument).getFormattedMessage().contains(token);
			}
		};
	}
}
