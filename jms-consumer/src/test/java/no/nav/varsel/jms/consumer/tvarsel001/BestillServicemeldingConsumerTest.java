package no.nav.varsel.jms.consumer.tvarsel001;

import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.PersonIdent;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.JmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static no.nav.varsel.Utils.formatDateTime;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.MOTTAKER;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.UTLOEPSTIDSPUNKT_LDT;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VARSELTYPE_ID;
import static no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapperTest.PERSON_IDENT;
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
import static org.junit.jupiter.api.Assertions.assertNull;

// Testen er altfor brittle. Burde skrives om.
public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	@Autowired
	private Queue bestillServicemeldingQueue;

	@Test
	public void shouldReceieveJms() {
		JmsReply response = sendMessage(bestillServicemeldingQueue, createVarsel());

		isOk(response);
		await().atMost(ofSeconds(5)).untilAsserted(() ->
				assertThat(varselbestillingRepo.count(), is(1L))
		);

		String varselTekst = FOERSTE_GANG_TEKST.replace("{mottaker}", VAL);

		String varselId = assertDb(varselTekst).getVarselId();
	}

	@Test
	public void shouldTrimKontaktInfo() {
		JAXBElement<Varsel> varsel = createVarsel();
		PersonIdent personIdent = new PersonIdent();
		personIdent.setPersonIdent(PERSONIDENT_WHITESPACE_TEST);
		varsel.getValue().setMottaker(personIdent);
		JmsReply response = sendMessage(bestillServicemeldingQueue, varsel);
		isOk(response);
	}

	@Test
	public void shouldPutOnBackoutIfFailedWs() {
		JAXBElement<Varsel> varsel = createVarsel();
		stubPdlConsumerTechnicalErrorWithInternalServerError();
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);
		isOk(response);
	}

	@Test
	public void shouldNotPutOnBackoutIfFailedWsFunksjonell() {
		JAXBElement<Varsel> varsel = createVarsel();
		stubPdlConsumerFunctionalErrorWithInternalServerError();
		JmsReply response = sendMessage(bestillServicemeldingQueue, varsel);
		isOk(response);

		Object backout = receive(backoutQueue);
		assertNull(backout);
	}

	@Test
	public void shouldPutOnBackoutAndRollbackIfFailedAfterDbSave() {
		stubVarselInfoV1();
		stubPdlConsumer();
		JAXBElement<Varsel> varsel = createVarsel();
		varsel.getValue().getVarslingstype().setValue(FEIL_MQ_UT);
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(0L));
	}

	public static JAXBElement<Varsel> createVarsel() {
		return new ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
	}

	private no.nav.varsel.domain.object.Varsel assertDb(String varselTekst) {
		Varselbestilling varselbestilling = varselbestillingRepo.findAllEager().get(0);
		assertThat(UUID.fromString(varselbestilling.getVarselbestillingId())
				.toString(), is(varselbestilling.getVarselbestillingId()));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(formatDateTime(varselbestilling.getUtlopTidspunkt()), is(equalTo(formatDateTime(UTLOEPSTIDSPUNKT_LDT))));
		assertThat(varselbestilling.getFnr(), is(PERSON_IDENT));
		assertThat(varselbestilling.getAktorId(), is(MOTTAKER));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), nullValue());
		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
		assertThat(varselbestilling.getNesteVarslingDato(), nullValue());
		assertThat(varselbestilling.getChangeStamp().getOpprettetAv(), is(JmsConsumer.BESTILL_SERVICEMELDING.getServiceName()));
		assertThat(varselbestilling.getChangeStamp().getOpprettetDato(), aboutNow());

		assertThat(varselbestilling.getVarsels(), hasSize(1));

		no.nav.varsel.domain.object.Varsel varsel = varselbestilling.getVarsels().iterator().next();
		assertThat(UUID.fromString(varsel.getVarselId()).toString(), is(varsel.getVarselId()));
		assertThat(varsel.getKanal(), is(KanalCode.EPOST));
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getDistribusjonTidspunkt(), nullValue());
		assertThat(varsel.getKontaktInfo(), is(EPOSTADRESSE));
		assertThat(varsel.getStatus(), is(StatusCode.SENDT));
		assertThat(varsel.getFeilbeskrivelse(), nullValue());
		assertThat(varsel.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarselTekst(), is(varselTekst));
		assertThat(varsel.getVarselUrl(), nullValue());
		assertThat(varsel.getErRevarsel(), is(false));
		assertThat(varsel.getChangeStamp().getOpprettetAv(), is(JmsConsumer.BESTILL_SERVICEMELDING.getServiceName()));
		assertThat(varsel.getChangeStamp().getOpprettetDato(), aboutNow());

		return varsel;
	}
}