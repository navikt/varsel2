package no.nav.varsel.jms.consumer.tvarsel006;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ObjectFactory;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Person;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ServicemeldingMedKontaktinformasjon;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.JmsConsumer;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.test.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static no.nav.varsel.Utils.formatDateTime;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.jms.consumer.AbstractJmsConsumer.JMS_NOBACKOUTLOG;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.AKTOER_ID;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.EPOST;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.ORGNUMMER;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.PERSON_IDENT;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.UTLOEPS_TIDSPUNKT;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.VARSELTYPE_ID;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.createServicemeldingMedKontaktinformasjon;
import static no.nav.varsel.jms.producer.VarselutsendingProducer.FEIL_MQ_UT;
import static no.nav.varsel.repo.TestdataUtil.PERSONIDENT_WHITESPACE_TEST;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.EPOSTADRESSE;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_TITTEL;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class BestillServicemeldingMedKontaktInfoConsumerTest extends AbstractConsumerJmsTest {

	private TestUtils.MockAppender loggerMock = null;

	@Autowired
	private Queue bestillServicemeldingKontaktInfoQueue;

	@Autowired
	private Queue varselutsendingQueue;

	@BeforeEach
	public void setUp() {
		loggerMock = TestUtils.getMockedAppender(JMS_NOBACKOUTLOG);
	}

	@Test
	public void shouldNotPutInvalidInputOnBackout() {
		ServicemeldingMedKontaktinformasjon varselBestilling = createServicemeldingMedKontaktinformasjon();
		varselBestilling.setVarseltypeId(null);
		JmsReply response = sendMessage(bestillServicemeldingKontaktInfoQueue, new ObjectFactory().createServicemelding(varselBestilling));

		isOk(response);

		loggerMock.verify("Nonbackout Error in service=tvarsel006");
	}

	@Test
	public void shouldNotPutOnBackoutIfFailedWsFunksjonell() {
		JAXBElement<ServicemeldingMedKontaktinformasjon> servicemelding = new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon());
		stubPdlConsumerNotFound();

		JmsReply response = sendMessage(bestillServicemeldingKontaktInfoQueue, servicemelding);

		isOk(response);

		loggerMock.verify("Nonbackout Error in service=tvarsel006");
	}

	@Test
	public void shouldPutOnBackoutIfFailedWs() {
		JAXBElement<ServicemeldingMedKontaktinformasjon> servicemelding = new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon());
		stubPdlConsumerTechnicalErrorWithInternalServerError();
		Message response = sendMessageListenBoq(bestillServicemeldingKontaktInfoQueue, servicemelding);

		isOk(response);
	}

	@Test
	public void shouldPutOnBackoutAndRollbackIfFailedAfterDbSave() {
		JAXBElement<ServicemeldingMedKontaktinformasjon> servicemelding = new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon());
		servicemelding.getValue().setVarseltypeId(FEIL_MQ_UT);
		Message response = sendMessageListenBoq(bestillServicemeldingKontaktInfoQueue, servicemelding);

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(0L));
	}

	@Test
	public void shouldReceiveJms() {
		JmsReply response = sendMessage(bestillServicemeldingKontaktInfoQueue, new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon()));

		isOk(response);
		await().atMost(ofSeconds(5)).untilAsserted(() -> {
			assertThat(varselbestillingRepo.count(), is(1L));
		});

		String varselTekst = FOERSTE_GANG_TEKST.replace("{mottaker}", VAL);
		String varselId = assertDb(varselTekst).getVarselId();
		assertVarselutsendingQueue(varselTekst, varselId);
	}

	@Test
	public void shouldTrimKontaktInfo() {
		JAXBElement<ServicemeldingMedKontaktinformasjon> serviceMelding = new ObjectFactory().createServicemelding(createServicemeldingMedKontaktinformasjon());
		Person person = new Person();
		person.setIdent(PERSONIDENT_WHITESPACE_TEST);

		serviceMelding.getValue().setMottaker(person);

		JmsReply response = sendMessage(bestillServicemeldingKontaktInfoQueue, serviceMelding);

		isOk(response);
		await().atMost(ofSeconds(5)).untilAsserted(() -> {
			assertThat(varselbestillingRepo.count(), is(1L));
		});
		Varselutsending varselutsending = receive(varselutsendingQueue);
		assertThat(varselutsending.getDistribusjon().getKontaktinformasjon(), equalTo(EPOSTADRESSE));
	}

	private no.nav.varsel.domain.object.Varsel assertDb(String varselTekst) {
		Varselbestilling varselbestilling = varselbestillingRepo.findAllEager().get(0);
		assertThat(UUID.fromString(varselbestilling.getVarselbestillingId()).toString(), is(varselbestilling.getVarselbestillingId()));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(formatDateTime(varselbestilling.getUtlopTidspunkt()), is(equalTo(formatDateTime((UTLOEPS_TIDSPUNKT)))));
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
		Varselutsending varselutsending = findLastMessage(varselutsendingQueue);

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