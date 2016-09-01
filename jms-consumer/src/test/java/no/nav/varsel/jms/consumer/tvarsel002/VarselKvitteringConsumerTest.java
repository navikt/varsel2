package no.nav.varsel.jms.consumer.tvarsel002;

import static no.nav.varsel.jms.consumer.tvarsel002.VarselKvitteringConsumer.TVARSEL002;
import static no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapperTest.DATE_UTSENDINGSSTIDSPUNKT;
import static no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapperTest.FEILMELDING;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.ObjectFactory;
import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.UUID;

/**
 * Itest for {@link VarselKvitteringConsumer}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class VarselKvitteringConsumerTest extends AbstractConsumerJmsTest {
	private static final String STATUS_ERROR = "Error";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Inject
	private Queue varselKvitteringQueue;
	@Inject
	private Queue bestillServicemeldingQueue;

	@Test
	public void shouldPersistPlukketKvitteringsmelding() throws Exception {
		Varselbestilling varselbestilling = persistVarselbestilling();
		String varselId = varselbestilling.getVarsels().iterator().next().getVarselId();

		JAXBElement<VarselKvittering> varselKvittering = createVarselKvitteringJaxBElement(varselId);
		JmsReply response = sendMessage(varselKvitteringQueue, varselKvittering);
		assertMessageNotOnBq(response);

		assertPlukketVarselScenario(varselId);
	}

	@Test
	public void shouldPersistFeiletKvitteringsmelding() throws Exception {
		Varselbestilling varselbestilling = persistVarselbestilling();
		String varselId = varselbestilling.getVarsels().iterator().next().getVarselId();

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
		Varselbestilling varselbestilling = persistVarselbestilling();
		String varselId = varselbestilling.getVarsels().iterator().next().getVarselId();

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
		Varselbestilling varselbestilling = persistVarselbestilling();
		String varselId = varselbestilling.getVarsels().iterator().next().getVarselId();

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

	private Varselbestilling persistVarselbestilling() {
		JmsReply createVarselresponse = sendMessage(bestillServicemeldingQueue, createVarsel());
		assertTrue(createVarselresponse != null && createVarselresponse.isOk());
		List<Varselbestilling> varselbestillings = varselbestillingRepo.findAllEager();
		assertThat(varselbestillings, hasSize(1));
		Varselbestilling varselbestilling = varselbestillings.iterator().next();
		assertThat(varselbestilling.getVarsels(), hasSize(1));
		return varselbestilling;
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