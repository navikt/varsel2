package no.nav.varsel.tvarsel001.itest;

import no.nav.brukernotifikasjon.schemas.builders.BeskjedInputBuilder;
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static no.nav.doknotifikasjon.schemas.PrefererteKanal.EPOST;
import static no.nav.doknotifikasjon.schemas.PrefererteKanal.SMS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("itest")
@Import({
		NotifikasjonTestConsumer.class,
		BrukernotifikasjonTestConsumer.class,
		KafkaEventProducer.class,
		CustomKafkaTemplate.class
})
@EmbeddedKafka(
		partitions = 1,
		topics = {
				"teamdokumenthandtering.privat-dok-notifikasjon",
				"min-side.aapen-brukernotifikasjon-beskjed-v1"
		},
		controlledShutdown = true,
		brokerProperties = {
				"listeners=PLAINTEXT://127.0.0.1:60172",
				"port=60172",
				"offsets.topic.replication.factor=1",
				"transaction.state.log.replication.factor=1",
				"transaction.state.log.min.isr=1"
		}
)
@SpringBootTest(classes = {Tvarsel001ITest.class})
@EnableAutoConfiguration
public class Tvarsel001ITest {

	private static final String FNR = "12345678910";
	private static final String BESTILLINGSID = "beaa22a6-6233-4d9b-97c0-fc6b174f2a60";
	private static final String NOTIFIKASJON_TOPIC = "teamdokumenthandtering.privat-dok-notifikasjon";
	private static final String BRUKERNOTIFIKASJON_TOPIC = "min-side.aapen-brukernotifikasjon-beskjed-v1";
	private static final String NAMESPACE = "teamdokumenthandtering";
	private static final String APPNAVN = "varsel";
	private static final Integer SIKKERHETSNIVAA = 3;
	private static final LocalDateTime TIDSPUNKT = LocalDateTime.now();

	@Autowired
	private KafkaEventProducer kafkaEventProducer;

	@Autowired
	private NotifikasjonTestConsumer notifikasjonTestConsumer;

	@Autowired
	private BrukernotifikasjonTestConsumer brukernotifikasjonTestConsumer;

	@Test
	public void shouldPublishNotifikasjonOK() throws InterruptedException {

		publishNotifikasjon(doknotifikasjon());

		notifikasjonTestConsumer.getLatch().await(10, TimeUnit.SECONDS);

		assertEquals(notifikasjonTestConsumer.getLatch().getCount(), 0L);
		assertEquals(NOTIFIKASJON_TOPIC, notifikasjonTestConsumer.getConsumerRecord().topic());
		assertEquals("key", notifikasjonTestConsumer.getConsumerRecord().key());
		assertEquals(doknotifikasjon(), notifikasjonTestConsumer.getConsumerRecord().value());
	}

	private void publishNotifikasjon(Doknotifikasjon doknotifikasjon) {
		kafkaEventProducer.publish(
				NOTIFIKASJON_TOPIC,
				"key",
				doknotifikasjon
		);
	}

	@Test
	public void shouldPublishBrukernotifikasjonOK() throws InterruptedException {

		publishBrukernotifikasjon(beskjedInput(), nokkelInput());

		brukernotifikasjonTestConsumer.getLatch().await(10, TimeUnit.SECONDS);

		assertEquals(brukernotifikasjonTestConsumer.getLatch().getCount(), 0L);
		assertEquals(BRUKERNOTIFIKASJON_TOPIC, brukernotifikasjonTestConsumer.getConsumerRecord().topic());
		assertEquals(nokkelInput(), brukernotifikasjonTestConsumer.getConsumerRecord().key());
		assertEquals(beskjedInput(), brukernotifikasjonTestConsumer.getConsumerRecord().value());
	}

	private void publishBrukernotifikasjon(BeskjedInput beskjedInput, NokkelInput nokkelInput) {
		kafkaEventProducer.publish(
				BRUKERNOTIFIKASJON_TOPIC,
				nokkelInput,
				beskjedInput
		);
	}

	private Doknotifikasjon doknotifikasjon() {
		var SMS_TEKST = """
				Hei! Her er en sms fra NAV. Mvh NAV
				""";
		var EPOST_TEKST = """
				<!DOCTYPE html>
				<html>
				<body>
				<p>Hei!</p>
				<p>Her er en e-post fra NAV</p>
				<p>Mvh</p>
				<p>NAV</p>
				</body>
				</html>
				""";
		return Doknotifikasjon.newBuilder()
				.setBestillingsId(BESTILLINGSID)
				.setBestillerId(APPNAVN)
				.setFodselsnummer(FNR)
				.setTittel("Tittel")
				.setEpostTekst(EPOST_TEKST)
				.setSmsTekst(SMS_TEKST)
				.setPrefererteKanaler(Stream.of(EPOST, SMS).toList())
				.setSikkerhetsnivaa(3)
				.build();
	}

	private BeskjedInput beskjedInput() {
		return new BeskjedInputBuilder()
				.withTidspunkt(TIDSPUNKT)
				.withTekst("Varseltekst")
				.withLink(url())
				.withSikkerhetsnivaa(SIKKERHETSNIVAA)
				.withEksternVarsling(false)
				.build();
	}

	private NokkelInput nokkelInput() {
		return new NokkelInputBuilder()
				.withEventId(BESTILLINGSID)
				.withGrupperingsId(BESTILLINGSID)
				.withFodselsnummer(FNR)
				.withNamespace(NAMESPACE)
				.withAppnavn(APPNAVN)
				.build();
	}

	private URL url() {
		try {
			return new URL("https://www.varsel.com");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}

