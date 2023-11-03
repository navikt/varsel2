package no.nav.varsel.jms.consumer.tvarsel001;

import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.xml.bind.JAXBElement;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.JmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.namespace.QName;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static no.nav.varsel.Utils.formatDateTime;
import static no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.EPOSTADRESSE;
import static no.nav.varsel.consumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST;
import static no.nav.varsel.consumer.dokkat.VarselInfoConsumerTest.VARSEL_TITTEL;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.MOTTAKER;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.UTLOEPSTIDSPUNKT_LDT;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VARSELTYPE_ID;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	@Autowired
	private Queue bestillServicemeldingQueue;

	@Autowired
	private Queue bestillServicemeldingFunksjonellFeilQueue;
	public static final String PERSON_IDENT = "1234567890123";

	@Test
	@Disabled("Testen feiler på oppsett av Kafka. Vil i utgangspunktet stoppe testen før den kommer dit.")
	public void shouldReceieveJms() {
		stubPdlConsumer();
		JmsReply response = sendMessage(bestillServicemeldingQueue, createVarsel());

		isOk(response);
		await().atMost(ofSeconds(5)).untilAsserted(() ->
				assertThat(varselbestillingRepo.count(), is(1L))
		);

		String varselTekst = FOERSTE_GANG_TEKST.replace("{mottaker}", VAL);

		assertDb(varselTekst);
	}

	@Test
	public void shouldPutOnBackoutIfFailedWs() {
		JAXBElement<XMLVarsel> varsel = createVarsel();
		stubVarselInfoV1();
		stubPdlConsumerTechnicalErrorWithInternalServerError();

		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		isOk(response);
	}

	@Test
	public void shouldNotPutOnBackoutButOnTheOtherOneIfFailedWsFunksjonell() {
		JAXBElement<XMLVarsel> varsel = createVarsel();
		stubVarselInfoV1();
		stubPdlConsumerFunctionalErrorWithInternalServerError();
		JmsReply response = sendMessage(bestillServicemeldingQueue, varsel);

		Object backout = receive(backoutQueue);
		assertNull(backout);
	}

	@Test
	public void shouldPutOnBackoutAndRollbackIfFailedAfterDbSave() {
		stubVarselInfoV1();
		stubPdlConsumer();
		JAXBElement<XMLVarsel> varsel = createVarsel();
		varsel.getValue().getVarslingstype().setValue(FEIL_MQ_UT);
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(0L));
	}

	public static JAXBElement<XMLVarsel> createVarsel() {
		XMLVarsel varsel = BestillServicemeldingMapperTest.createVarsel();

		return new JAXBElement<>(new QName("http://nav.no/melding/virksomhet/varsel/v1/varsel", "Varsel"), XMLVarsel.class, null, varsel);
	}

	private Varsel assertDb(String varselTekst) {
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