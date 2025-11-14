package no.nav.varsel.tvarsel001;

import lombok.extern.slf4j.Slf4j;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Import(KafkaEventProducer.class)
public class BrukernotifikasjonBeskjedPublisher {

	public static final String KAFKA_TOPIC_AAPEN_BRUKERNOTIFIKASJON_BESKJED = "min-side.aapen-brukervarsel-v1";
	private final KafkaEventProducer kafkaEventProducer;

	public BrukernotifikasjonBeskjedPublisher(KafkaEventProducer kafkaEventProducer) {
		this.kafkaEventProducer = kafkaEventProducer;
	}

	public void sendNotifikasjon(String varselId, String opprettVarselJson) {
		log.info("Sender brukernotifikasjon med bestillingsId={} til topic={}", varselId, KAFKA_TOPIC_AAPEN_BRUKERNOTIFIKASJON_BESKJED);
		kafkaEventProducer.publish(KAFKA_TOPIC_AAPEN_BRUKERNOTIFIKASJON_BESKJED, varselId, opprettVarselJson);
	}
}
