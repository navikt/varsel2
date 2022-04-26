package no.nav.varsel.tvarsel001.itest;

import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.varsel.kafka.KafkaEventProducer;
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

import static no.nav.doknotifikasjon.schemas.PrefererteKanal.SMS;
import static no.nav.doknotifikasjon.schemas.PrefererteKanal.EPOST;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("itest")
@Import({
		KafkaTestConsumer.class,
		KafkaEventProducer.class
})
@EmbeddedKafka(
		partitions = 1,
		topics = {
				"teamdokumenthandtering.privat-dok-notifikasjon-test"
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tvarsel001ITest {

	private static final String FNR = "12345678910";
	private static final String TOPIC = "teamdokumenthandtering.privat-dok-notifikasjon-test";

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

		publishMessage(doknotifikasjon());

		kafkaTestConsumer.getLatch().await(10, TimeUnit.SECONDS);

		assertEquals(kafkaTestConsumer.getLatch().getCount(), 0L);
		assertEquals(TOPIC, kafkaTestConsumer.getConsumerRecord().topic());
		assertEquals("key", kafkaTestConsumer.getConsumerRecord().key());
		assertEquals(doknotifikasjon(), kafkaTestConsumer.getConsumerRecord().value());
	}

	private void publishMessage(Doknotifikasjon doknotifikasjon) {
		kafkaEventProducer.publish(
				TOPIC,
				"key",
				doknotifikasjon
		);
	}

	private Doknotifikasjon doknotifikasjon() {
		return Doknotifikasjon.newBuilder()
				.setBestillingsId("12345")
				.setBestillerId("varsel")
				.setFodselsnummer(FNR)
				.setTittel("Tittel")
				.setEpostTekst(EPOST_TEKST)
				.setSmsTekst(SMS_TEKST)
				.setPrefererteKanaler(Stream.of(EPOST, SMS).toList())
				.setSikkerhetsnivaa(3)
				.build();
	}
}

