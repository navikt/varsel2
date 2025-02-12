package no.nav.varsel.tvarsel001;

import lombok.extern.slf4j.Slf4j;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Import(KafkaEventProducer.class)
public class BrukernotifikasjonBeskjedPublisher {

	public static final String KAFKA_TOPIC_AAPEN_BRUKERNOTIFIKASJON_BESKJED = "min-side.aapen-brukernotifikasjon-beskjed-v1";
	private final KafkaEventProducer kafkaEventProducer;

	public BrukernotifikasjonBeskjedPublisher(KafkaEventProducer kafkaEventProducer) {
		this.kafkaEventProducer = kafkaEventProducer;
	}

	public void sendNotifikasjon(BeskjedInput beskjedInput, NokkelInput nokkelInput) {

		log.info("Sender brukernotifikasjon med bestillingsId={} til topic={}", nokkelInput.getEventId(), KAFKA_TOPIC_AAPEN_BRUKERNOTIFIKASJON_BESKJED);
		kafkaEventProducer.publish(KAFKA_TOPIC_AAPEN_BRUKERNOTIFIKASJON_BESKJED, nokkelInput, beskjedInput);
	}
}
