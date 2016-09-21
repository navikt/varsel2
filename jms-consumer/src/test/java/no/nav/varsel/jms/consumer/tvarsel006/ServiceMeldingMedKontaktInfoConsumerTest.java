package no.nav.varsel.jms.consumer.tvarsel006;

import static no.nav.varsel.jms.consumer.AbstractJmsConsumer.JMS_NOBACKOUTLOG;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.createServicemeldingMedKontaktinformasjon;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ObjectFactory;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ServicemeldingMedKontaktinformasjon;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapperTest;
import no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapper;
import no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest;
import no.nav.varsel.test.TestUtils;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;

/**
 * ITest for {@link ServiceMeldingMedKontaktInfoConsumer}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class ServiceMeldingMedKontaktInfoConsumerTest extends AbstractConsumerJmsTest {

	private TestUtils.MockAppender loggerMock = TestUtils.getMockedAppender(JMS_NOBACKOUTLOG);

	@Inject
	private Queue bestillServicemeldingKontaktInfoQueue;
	@Inject
	private Queue varselutsendingQueue;

	@Test
	public void shouldValidateInput() throws Exception {
		ServicemeldingMedKontaktinformasjon servicemelding = createServicemeldingMedKontaktinformasjon();
		servicemelding.setVarseltypeId(null);

		JAXBElement<ServicemeldingMedKontaktinformasjon> servicemeldingJaxB =  new ObjectFactory().createServicemelding(servicemelding);

		sendMessage(bestillServicemeldingKontaktInfoQueue, servicemeldingJaxB);
		receive(varselutsendingQueue);
		loggerMock.verify("test");

		//TODO: Logmock

	}
}