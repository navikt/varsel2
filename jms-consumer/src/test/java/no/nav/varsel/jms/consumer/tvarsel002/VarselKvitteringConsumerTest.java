package no.nav.varsel.jms.consumer.tvarsel002;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.ObjectFactory;
import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.factory.annotation.Autowired;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.time.Duration;
import java.util.UUID;

import static no.nav.varsel.jms.consumer.tvarsel002.VarselKvitteringConsumer.TVARSEL002;
import static no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapperTest.DATE_UTSENDINGSSTIDSPUNKT;
import static no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapperTest.FEILMELDING;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Itest for {@link VarselKvitteringConsumer}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class VarselKvitteringConsumerTest extends AbstractConsumerJmsTest {
	private static final String STATUS_ERROR = "Error";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired
	private Queue varselKvitteringQueue;
	@Autowired
	private Queue bestillServicemeldingQueue;

	@Test
	public void shouldPersistPlukketKvitteringsmelding() throws Exception {
		sendVarselbestilling();
		String varselId = getVarselId();

		JAXBElement<VarselKvittering> varselKvittering = createVarselKvitteringJaxBElement(varselId);
		JmsReply response = sendMessage(varselKvitteringQueue, varselKvittering);
		assertMessageNotOnBq(response);

		assertPlukketVarselScenario(varselId);
	}

	@Test
	public void shouldPersistFeiletKvitteringsmelding() throws Exception {
		sendVarselbestilling();
		String varselId = getVarselId();

		VarselKvittering kvittering = createVarselKvittering(varselId);
		kvittering.setStatus(STATUS_ERROR);
		JAXBElement<VarselKvittering> varselKvittering = createVarselKvitteringJaxBElement(kvittering);
		JmsReply response = sendMessage(varselKvitteringQueue, varselKvittering);
		assertMessageNotOnBq(response);

		no.nav.varsel.domain.object.Varsel varsel = varselRepo.findByVarselId(varselId);
		assertThat(varsel.getStatus(), is(StatusCode.FEILET));
		assertThat(varsel.getFeilbeskrivelse(), is(FEILMELDING));
		assertThat(varsel.getKvitteringTidspunkt(), is(aboutNow()));
		assertThat(varsel.getChangeStamp().getEndretAv(), is(TVARSEL002));
		assertThat(varsel.getChangeStamp().getEndretDato(), is(aboutNow()));
	}

	@Test
	public void shouldNotPutInvalidEmptyKvitteringOnBq() throws Exception {
		JmsReply response = sendMessage(varselKvitteringQueue, createVarselKvitteringJaxBElement(new VarselKvittering()));

		assertMessageNotOnBq(response);
	}

	@Test
	public void shouldNotPutDuplicateKvitteringOnBq() throws Exception {
		sendVarselbestilling();
		String varselId = getVarselId();

		JAXBElement<VarselKvittering> varselKvittering = createVarselKvitteringJaxBElement(varselId);
		JmsReply response = sendMessage(varselKvitteringQueue, varselKvittering);
		assertMessageNotOnBq(response);

		response = sendMessage(varselKvitteringQueue, varselKvittering);
		assertMessageNotOnBq(response);
		assertPlukketVarselScenario(varselId);
	}

	@Test
	public void shouldNotPutNonExistingVarselIdOnBq() throws Exception {
		JAXBElement<VarselKvittering> varselKvittering = createVarselKvitteringJaxBElement(UUID.randomUUID().toString());

		JmsReply response = sendMessage(varselKvitteringQueue, varselKvittering);
		assertMessageNotOnBq(response);
	}

	@Test
	public void shouldNotPutInvalidKvitteringstatusOnBq() throws Exception {
		sendVarselbestilling();
		String varselId = getVarselId();

		VarselKvittering kvittering = createVarselKvittering(varselId);
		kvittering.setStatus("invalid status");
		JAXBElement<VarselKvittering> varselKvittering = createVarselKvitteringJaxBElement(kvittering);
		JmsReply response = sendMessage(varselKvitteringQueue, varselKvittering);
		assertMessageNotOnBq(response);
	}

	private void assertPlukketVarselScenario(String varselId) {
		no.nav.varsel.domain.object.Varsel varsel = varselRepo.findByVarselId(varselId);
		assertThat(varsel.getStatus(), is(StatusCode.FERDIGBEHANDLET));
		assertThat(varsel.getDistribusjonTidspunkt(), is(DATE_UTSENDINGSSTIDSPUNKT));
		assertThat(varsel.getKvitteringTidspunkt(), is(aboutNow()));
		assertThat(varsel.getFeilbeskrivelse(), is(nullValue()));
		assertThat(varsel.getChangeStamp().getEndretAv(), is(TVARSEL002));
		assertThat(varsel.getChangeStamp().getEndretDato(), is(aboutNow()));
	}

	private void assertMessageNotOnBq(JmsReply response) {
		//Response is not Ok when message goes to backout
		assertThat(response, notNullValue());
		assertThat(response.isOk(), is(true));
	}

	private void sendVarselbestilling() {
		JmsReply createVarselresponse = sendMessage(bestillServicemeldingQueue, createVarsel());
		assertTrue(createVarselresponse != null && createVarselresponse.isOk());
	}

	private String getVarselId() {
		await().atMost(Duration.ofSeconds(5)).until(() -> varselbestillingRepo.findAllEager().size() == 1);
		return varselbestillingRepo.findAllEager().iterator().next().getVarsels().iterator().next().getVarselId();
	}

	public static JAXBElement<VarselKvittering> createVarselKvitteringJaxBElement(String varselId) {
		return createVarselKvitteringJaxBElement(createVarselKvittering(varselId));
	}

	public static JAXBElement<VarselKvittering> createVarselKvitteringJaxBElement(VarselKvittering varselKvittering) {
		return new ObjectFactory().createVarselKvittering(varselKvittering);
	}

	private static VarselKvittering createVarselKvittering(String varselId) {
		VarselKvittering varselKvittering = MottaVarselKvitteringMapperTest.createVarselKvittering();
		varselKvittering.setVarselId(varselId);
		return varselKvittering;
	}

	public static JAXBElement<Varsel> createVarsel() {
		return new no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
	}
}