package no.nav.varsel.jms.consumer.tvarsel001;

import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.xml.bind.JAXBElement;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.namespace.QName;

import static java.time.Duration.ofSeconds;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.EPOSTADRESSE;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.StatusCode.SENDT;
import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.MOTTAKER;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.UTLOEPSTIDSPUNKT_LDT;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VARSELTYPE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	private static final String VARSEL_TITTEL = "Varsel Tittel";
	private static final String FOERSTE_GANG_TEKST = "Første gang tekst til {mottaker}";
	private static final String PERSON_IDENT = "1234567890123";

	@Autowired
	private Queue bestillServicemeldingQueue;

	@MockBean
	private KafkaEventProducer kafkaEventProducer;

	@Test
	public void shouldReceiveJms() {
		stubPdl();
		stubDokmet();
		stubDigdir();
		doNothing().when(kafkaEventProducer).publish(any(String.class), any(String.class), any(Doknotifikasjon.class));
		doNothing().when(kafkaEventProducer).publish(any(String.class), any(NokkelInput.class), any(BeskjedInput.class));

		await().atMost(ofSeconds(5)).untilAsserted(() -> {
					JmsReply response = sendMessage(bestillServicemeldingQueue, createVarsel());
					isOk(response);
					assertThat(varselbestillingRepo.count()).isEqualTo(1);

					String varselTekst = FOERSTE_GANG_TEKST.replace("{mottaker}", VAL);

					assertDb(varselTekst);
				}
		);
	}

	@Test
	public void shouldPutOnFunctionalBoqIfBadRequestFromPdl() {
		stubPdl(BAD_REQUEST);
		JAXBElement<XMLVarsel> varsel = createVarsel();

		await().atMost(ofSeconds(5)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnFunctionalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	@Test
	public void shouldPutOnTechnicalBoqIfInternalServerErrorFromPdl() {
		stubPdl(INTERNAL_SERVER_ERROR);
		JAXBElement<XMLVarsel> varsel = createVarsel();

		await().atMost(ofSeconds(5)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnTechnicalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	@Test
	public void shouldPutOnFunctionalBoqIfNotFoundFromDokmet() {
		stubPdl();
		stubDokmet(NOT_FOUND);

		JAXBElement<XMLVarsel> varsel = createVarsel();

		await().atMost(ofSeconds(5)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnFunctionalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	@Test
	public void shouldPutOnTechnicalBoqIfInternalServerErrorFromDokmet() {
		stubPdl();
		stubDokmet(INTERNAL_SERVER_ERROR);
		JAXBElement<XMLVarsel> varsel = createVarsel();

		await().atMost(ofSeconds(5)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnTechnicalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	public static JAXBElement<XMLVarsel> createVarsel() {
		XMLVarsel varsel = BestillServicemeldingMapperTest.createVarsel();

		return new JAXBElement<>(new QName("http://nav.no/melding/virksomhet/varsel/v1/varsel", "Varsel"), XMLVarsel.class, null, varsel);
	}

	// For å unngå tidssoneproblematikk på GHA er tidspunktene sjekket til å være innenfor et 3-timersintervall
	private Varsel assertDb(String varselTekst) {
		final var tidspunktMedTidssonebuffer = 3;

		Varselbestilling varselbestilling = varselbestillingRepo.findAllEager().get(0);
		assertThat(varselbestilling.getVarseltypeId()).isEqualTo(VARSELTYPE_ID);
		assertThat(varselbestilling.getUtlopTidspunkt()).isCloseTo(UTLOEPSTIDSPUNKT_LDT, within(tidspunktMedTidssonebuffer, HOURS));
		assertThat(varselbestilling.getFnr()).isEqualTo(PERSON_IDENT);
		assertThat(varselbestilling.getAktorId()).isEqualTo(MOTTAKER);
		assertThat(varselbestilling.getBestillingTidspunkt()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));
		assertThat(varselbestilling.getRevarslingIntervall()).isNull();
		assertThat(varselbestilling.getAntallRevarslinger()).isNull();
		assertThat(varselbestilling.getNesteVarslingDato()).isNull();
		assertThat(varselbestilling.getChangeStamp().getOpprettetAv()).isEqualTo(BESTILL_SERVICEMELDING.getServiceName());
		assertThat(varselbestilling.getChangeStamp().getOpprettetDato()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));

		assertThat(varselbestilling.getVarsels()).hasSize(1);
		no.nav.varsel.domain.object.Varsel varsel = varselbestilling.getVarsels().iterator().next();

		assertThat(varsel.getKanal()).isEqualTo(EPOST);
		assertThat(varsel.getSendtTidspunkt()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));
		assertThat(varsel.getDistribusjonTidspunkt()).isNull();
		assertThat(varsel.getKontaktInfo()).isEqualTo(EPOSTADRESSE);
		assertThat(varsel.getStatus()).isEqualTo(SENDT);
		assertThat(varsel.getFeilbeskrivelse()).isNull();
		assertThat(varsel.getVarselTittel()).isEqualTo(VARSEL_TITTEL);
		assertThat(varsel.getVarselTekst()).isEqualTo(varselTekst);
		assertThat(varsel.getVarselUrl()).isNull();
		assertThat(varsel.getErRevarsel()).isFalse();
		assertThat(varsel.getChangeStamp().getOpprettetAv()).isEqualTo(BESTILL_SERVICEMELDING.getServiceName());
		assertThat(varsel.getChangeStamp().getOpprettetDato()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));

		return varsel;
	}
}