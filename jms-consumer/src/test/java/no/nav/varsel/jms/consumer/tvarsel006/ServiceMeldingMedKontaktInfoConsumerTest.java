package no.nav.varsel.jms.consumer.tvarsel006;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.jms.consumer.AbstractJmsConsumer.JMS_NOBACKOUTLOG;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.AKTOER_ID;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.EPOST;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.ORGNUMMER;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.UTLOEPS_TIDSPUNKT;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.VARSELTYPE_ID;
import static no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapperTest.createServicemeldingMedKontaktinformasjon;
import static no.nav.varsel.jms.producer.VarselutsendingProducer.FEIL_MQ_UT;
import static no.nav.varsel.mock.AktoerV2Mock.PERSON_IDENT;
import static no.nav.varsel.repo.TestdataUtil.FUNKSJONELL_FEIL;
import static no.nav.varsel.repo.TestdataUtil.TEKNISK_FEIL;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_TITTEL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.AktoerId;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ObjectFactory;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ServicemeldingMedKontaktinformasjon;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.JmsConsumer;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.test.TestUtils;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.util.UUID;

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
	public void shouldNotPutInvalidInputOnBackout() throws Exception {
		ServicemeldingMedKontaktinformasjon varselBestilling = createServicemeldingMedKontaktinformasjon();
		varselBestilling.setVarseltypeId(null);
		JmsReply response = sendMessage(bestillServicemeldingKontaktInfoQueue, new ObjectFactory().createServicemelding(varselBestilling));

		isOk(response);

		loggerMock.verify("Nonbackout");
	}

	@Test
	public void shouldNotPutOnBackoutIfFailedWsFunksjonell() throws Exception {
		JAXBElement<ServicemeldingMedKontaktinformasjon> servicemelding = new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon());
		((AktoerId) servicemelding.getValue().getMottaker()).setAktoerId(FUNKSJONELL_FEIL);

		JmsReply response = sendMessage(bestillServicemeldingKontaktInfoQueue, servicemelding);

		isOk(response);

		loggerMock.verify("Nonbackout");
	}

	@Test
	public void shouldPutOnBackoutIfFailedWs() throws Exception {
		JAXBElement<ServicemeldingMedKontaktinformasjon> servicemelding = new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon());
		((AktoerId) servicemelding.getValue().getMottaker()).setAktoerId(TEKNISK_FEIL);
		Message response = sendMessageListenBoq(bestillServicemeldingKontaktInfoQueue, servicemelding);

		isOk(response);
	}

	@Test
	public void shouldPutOnBackoutAndRollbackIfFailedAfterDbSave() throws Exception {
		JAXBElement<ServicemeldingMedKontaktinformasjon> servicemelding = new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon());
		servicemelding.getValue().setVarseltypeId(FEIL_MQ_UT);
		Message response = sendMessageListenBoq(bestillServicemeldingKontaktInfoQueue, servicemelding);

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(0L));

		Object receive = receive(varselutsendingQueue);
		assertThat(receive, nullValue());
	}

	@Test
	public void shouldReceieveJms() throws Exception {
		JmsReply response = sendMessage(bestillServicemeldingKontaktInfoQueue, new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon()));

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(1L));

		String varselTekst = FOERSTE_GANG_TEKST.replace("{mottaker}", VAL);
		String varselId = assertDb(varselTekst).getVarselId();
		assertVarselutsendingQueue(varselTekst, varselId);
	}

	private no.nav.varsel.domain.object.Varsel assertDb(String varselTekst) {
		Varselbestilling varselbestilling = varselbestillingRepo.findAllEager().get(0);
		assertThat(UUID.fromString(varselbestilling.getVarselbestillingId()).toString(), is(varselbestilling.getVarselbestillingId()));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(equalTo(UTLOEPS_TIDSPUNKT)));
		assertThat(varselbestilling.getFnr(), is(PERSON_IDENT));
		assertThat(varselbestilling.getAktorId(), is(AKTOER_ID));
		assertThat(varselbestilling.getOrgNr(), is(ORGNUMMER));

		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), nullValue());
		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
		assertThat(varselbestilling.getNesteVarslingDato(), nullValue());
		assertThat(varselbestilling.getChangeStamp().getOpprettetAv(), is(JmsConsumer.BESTILL_SERVICEMELDING_KONTAKTINFO.getServiceName()));
		assertThat(varselbestilling.getChangeStamp().getOpprettetDato(), aboutNow());

		assertThat(varselbestilling.getVarsels(), hasSize(1));

		no.nav.varsel.domain.object.Varsel varsel = varselbestilling.getVarsels().iterator().next();
		assertThat(UUID.fromString(varsel.getVarselId()).toString(), is(varsel.getVarselId()));
		assertThat(varsel.getKanal(), is(KanalCode.EPOST));
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getDistribusjonTidspunkt(), nullValue());
		assertThat(varsel.getKontaktInfo(), is(EPOST));
		assertThat(varsel.getStatus(), is(StatusCode.SENDT));
		assertThat(varsel.getFeilbeskrivelse(), nullValue());
		assertThat(varsel.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarselTekst(), is(varselTekst));
		assertThat(varsel.getVarselUrl(), nullValue());
		assertThat(varsel.getErRevarsel(), is(false));
		assertThat(varsel.getChangeStamp().getOpprettetAv(), is(JmsConsumer.BESTILL_SERVICEMELDING_KONTAKTINFO.getServiceName()));
		assertThat(varsel.getChangeStamp().getOpprettetDato(), aboutNow());
		return varsel;
	}

	private void assertVarselutsendingQueue(String varselTekst, String varselId) {
		Varselutsending varselutsending = receive(varselutsendingQueue);

		assertThat(varselutsending.getVarselId(), is(varselId));
		assertThat(((no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Person)
				varselutsending.getMottaker()).getIdent(), is(PERSON_IDENT));
		assertThat(varselutsending.getUtloepstidspunkt(), equalTo(toXmlGregorianCalendar(UTLOEPS_TIDSPUNKT)));
		assertThat(varselutsending.getDistribusjon().getKanal().getValue(), is(KanalCode.EPOST.getKommunikasjonskanal()));
		assertThat(varselutsending.getDistribusjon().getKontaktinformasjon(), is(EPOST));
		assertThat(varselutsending.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(varselutsending.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varselutsending.getVarselTekst(), is(varselTekst));
		assertThat(varselutsending.getVarselURL(), nullValue());
	}
}