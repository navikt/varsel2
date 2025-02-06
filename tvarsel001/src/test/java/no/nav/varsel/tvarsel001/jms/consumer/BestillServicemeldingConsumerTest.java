package no.nav.varsel.tvarsel001.jms.consumer;

import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.xml.bind.JAXBElement;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.tvarsel001.jms.xml.JmsReply;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Set;

import static java.time.Duration.ofSeconds;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.EPOSTADRESSE;
import static no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.MOBILTELEFONNUMMER;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.domain.code.StatusCode.SENDT;
import static no.nav.varsel.tvarsel001.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.tvarsel001.jms.consumer.BestillServicemeldingMapperTest.MOTTAKER;
import static no.nav.varsel.tvarsel001.jms.consumer.BestillServicemeldingMapperTest.UTLOEPSTIDSPUNKT_LDT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

// Klassen har ustabilitet i testoppsettet, så alt utenom happy path er disabled.
// Ikke prøv å fikse dette. Tiden bør heller brukes på en omskriving av appen for å få et mer robust testoppsett.
@Disabled
public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	private static final String VARSELTYPEID_GRUPPEAKTIVITET = "Gruppeaktivitet";
	private static final String VARSELTYPEID_INDIVIDUELLSAMTALE = "IndividuellSamtale";
	private static final String PERSON_IDENT = "12345678901";
	private static final String DITT_NAV_VARSELTEKST = "Dette er en beskjed om at du har et møte på FA1 01.01.2025 klokken 12:00";
	private static final String SMS_VARSELTEKST = "Hei! Du har et møte i regi av NAV på FA1 01.01.2025 klokken 12:00. Vennlig hilsen NAV";
	private static final String EPOST_VARSELTEKST = "Hei! Dette er en beskjed om at du har et møte på FA1 01.01.2025 klokken 12:00. Vennlig hilsen NAV";
	private static final String VARSEL_URL = "http://nav.no";
	private static final String EPOST_TITTEL = "Påminnelse om møte";

	@Autowired
	private Queue bestillServicemeldingQueue;

	@MockBean
	private KafkaEventProducer kafkaEventProducer;

	@Test
	public void shouldReceiveJms() {
		stubPdl();
		stubDokmet();
		stubDigdir();
		doNothing().when(kafkaEventProducer).publish(any(String.class), any(NokkelInput.class), any(BeskjedInput.class));
		JAXBElement<XMLVarsel> varsel = createVarselWithVarseltypeId(VARSELTYPEID_GRUPPEAKTIVITET);

		await().atMost(ofSeconds(10)).untilAsserted(() -> {
					JmsReply response = sendMessage(bestillServicemeldingQueue, varsel);
					isOk(response);
					assertThat(varselbestillingRepo.count()).isEqualTo(1);

					assertVarselbestilling();
				}
		);
	}

	@Test
	@Disabled
	public void shouldPutOnFunctionalBoqIfBadRequestFromPdl() {
		stubPdl(BAD_REQUEST);
		JAXBElement<XMLVarsel> varsel = createVarselWithVarseltypeId(VARSELTYPEID_INDIVIDUELLSAMTALE);

		await().atMost(ofSeconds(10)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnFunctionalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	@Test
	@Disabled
	public void shouldPutOnTechnicalBoqIfInternalServerErrorFromPdl() {
		stubPdl(INTERNAL_SERVER_ERROR);
		JAXBElement<XMLVarsel> varsel = createVarselWithVarseltypeId(VARSELTYPEID_INDIVIDUELLSAMTALE);

		await().atMost(ofSeconds(10)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnTechnicalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	@ParameterizedTest
	@EnumSource(value = HttpStatus.class, names = {"BAD_REQUEST", "NOT_FOUND"})
	@Disabled
	public void shouldPutOnFunctionalBoqIf4xxFromDokmet(HttpStatus httpStatus) {
		stubPdl();
		stubDokmet(httpStatus);
		JAXBElement<XMLVarsel> varsel = createVarselWithVarseltypeId(VARSELTYPEID_INDIVIDUELLSAMTALE);

		await().atMost(ofSeconds(10)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnFunctionalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	@Test
	@Disabled
	public void shouldPutOnTechnicalBoqIf5xxFromDokmet() {
		stubPdl();
		stubDokmet(INTERNAL_SERVER_ERROR);
		JAXBElement<XMLVarsel> varsel = createVarselWithVarseltypeId(VARSELTYPEID_INDIVIDUELLSAMTALE);

		await().atMost(ofSeconds(10)).untilAsserted(() -> {
					Message response = sendMessageAndListenToResponseOnTechnicalBoq(bestillServicemeldingQueue, varsel);
					assertThat(response).isNotNull();
					assertThat(varselbestillingRepo.count()).isZero();
				}
		);
	}

	public static JAXBElement<XMLVarsel> createVarselWithVarseltypeId(String varseltypeId) {
		XMLVarsel varsel = BestillServicemeldingMapperTest.createVarsel(varseltypeId);

		return new JAXBElement<>(new QName("http://nav.no/melding/virksomhet/varsel/v1/varsel", "Varsel"), XMLVarsel.class, null, varsel);
	}

	// For å unngå tidssoneproblematikk på GHA er tidspunktene sjekket til å være innenfor et 3-timersintervall
	private void assertVarselbestilling() {
		final int tidspunktMedTidssonebuffer = 3;

		List<Varselbestilling> varselbestilling = varselbestillingRepo.findAllEager();

		assertThat(varselbestilling)
				.singleElement()
				.satisfies(vb -> {
					assertThat(vb.getVarseltypeId()).isEqualTo(VARSELTYPEID_GRUPPEAKTIVITET);
					assertThat(vb.getUtlopTidspunkt()).isCloseTo(UTLOEPSTIDSPUNKT_LDT, within(tidspunktMedTidssonebuffer, HOURS));
					assertThat(vb.getFnr()).isEqualTo(PERSON_IDENT);
					assertThat(vb.getAktorId()).isEqualTo(MOTTAKER);
					assertThat(vb.getBestillingTidspunkt()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));
					assertThat(vb.getRevarslingIntervall()).isNull();
					assertThat(vb.getAntallRevarslinger()).isNull();
					assertThat(vb.getNesteVarslingDato()).isNull();
					assertThat(vb.getChangeStamp().getOpprettetAv()).isEqualTo(BESTILL_SERVICEMELDING.getServiceName());
					assertThat(vb.getChangeStamp().getOpprettetDato()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));
				});

		Set<Varsel> varselListe = varselbestilling.getFirst().getVarsels();

		assertThat(varselListe)
				.hasSize(3)
				.extracting(Varsel::getKanal, Varsel::getKontaktInfo, Varsel::getVarselTittel, Varsel::getVarselTekst, Varsel::getVarselUrl)
				.containsExactlyInAnyOrder(
						tuple(DITT_NAV, null, null, DITT_NAV_VARSELTEKST, VARSEL_URL),
						tuple(SMS, MOBILTELEFONNUMMER, null, SMS_VARSELTEKST, null),
						tuple(EPOST, EPOSTADRESSE, EPOST_TITTEL, EPOST_VARSELTEKST, null));

		assertThat(varselListe)
				.allSatisfy(v -> {
					assertThat(v.getSendtTidspunkt()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));
					assertThat(v.getDistribusjonTidspunkt()).isNull();
					assertThat(v.getStatus()).isEqualTo(SENDT);
					assertThat(v.getFeilbeskrivelse()).isNull();
					assertThat(v.getErRevarsel()).isFalse();
					assertThat(v.getChangeStamp().getOpprettetAv()).isEqualTo(BESTILL_SERVICEMELDING.getServiceName());
					assertThat(v.getChangeStamp().getOpprettetDato()).isCloseTo(now(), within(tidspunktMedTidssonebuffer, HOURS));
				});
	}
}