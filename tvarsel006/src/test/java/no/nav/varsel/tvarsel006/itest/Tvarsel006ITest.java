package no.nav.varsel.tvarsel006.itest;

import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.kafka.KafkaEventProducer;
import no.nav.varsel.tvarsel006.itest.config.CustomAvroDeserializer;
import no.nav.varsel.tvarsel006.itest.config.CustomAvroSerializer;
import no.nav.varsel.tvarsel006.itest.config.KafkaTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("itest")
@Import({
		KafkaTestConsumer.class,
		KafkaTestConfig.class,
		KafkaEventProducer.class,
		CustomAvroSerializer.class,
		CustomAvroDeserializer.class
})
@EmbeddedKafka(
		partitions = 1,
		topics = {
				"privat-dok-notifikasjon-med-kontakt-info-test"
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
@SpringBootTest(classes = {Tvarsel006ITest.class})
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tvarsel006ITest {

	private static final String FNR = "12345678910";
	private static final String MOBILNR = "98765432";
	private static final String EPOST = "e@post.no";
	private static final String TOPIC = "privat-dok-notifikasjon-med-kontakt-info-test";

	private final String SMS_TEKST = """
			Hei! Her er en sms fra NAV. Mvh NAV
			""";

	private final String EPOST_TEKST = """
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

	@Autowired
	private KafkaEventProducer kafkaEventProducer;

	@Autowired
	private KafkaTestConsumer kafkaTestConsumer;

	@Test
	public void shouldPublishMessageOK() throws InterruptedException {

		publishMessage(notifikasjonMedkontaktInfo());

		kafkaTestConsumer.getLatch().await(10000, TimeUnit.MILLISECONDS);

		assertEquals(kafkaTestConsumer.getLatch().getCount(), 0L);
		assertEquals(TOPIC, kafkaTestConsumer.getConsumerRecord().topic());
		assertEquals("key", kafkaTestConsumer.getConsumerRecord().key());
		assertEquals(notifikasjonMedkontaktInfo(), kafkaTestConsumer.getConsumerRecord().value());
	}

	private void publishMessage(NotifikasjonMedkontaktInfo notifikasjonMedkontaktInfo) {
		kafkaEventProducer.publish(
				TOPIC,
				"key",
				notifikasjonMedkontaktInfo
		);
	}

	private NotifikasjonMedkontaktInfo notifikasjonMedkontaktInfo() {
		return NotifikasjonMedkontaktInfo.newBuilder()
				.setBestillingsId("12345")
				.setBestillerId("varsel")
				.setFodselsnummer(FNR)
				.setMobiltelefonnummer(MOBILNR)
				.setEpostadresse(EPOST)
				.setAntallRenotifikasjoner(0)
				.setRenotifikasjonIntervall(0)
				.setTittel("Melding fra NAV")
				.setEpostTekst(EPOST_TEKST)
				.setSmsTekst(SMS_TEKST)
				.setPrefererteKanaler(Stream.of(PrefererteKanal.EPOST, PrefererteKanal.SMS).toList())
				.build();
	}
}

