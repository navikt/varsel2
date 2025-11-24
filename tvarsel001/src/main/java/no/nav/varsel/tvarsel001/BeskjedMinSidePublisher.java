package no.nav.varsel.tvarsel001;

import lombok.extern.slf4j.Slf4j;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Import(KafkaEventProducer.class)
public class BeskjedMinSidePublisher {

	public static final String KAFKA_TOPIC_VARSEL_MIN_SIDE = "min-side.aapen-brukervarsel-v1";
	private final KafkaEventProducer kafkaEventProducer;

	public BeskjedMinSidePublisher(KafkaEventProducer kafkaEventProducer) {
		this.kafkaEventProducer = kafkaEventProducer;
	}

	public void sendBeskjedMinSide(String varselId, String opprettVarselJson) {
		log.info("Sender beskjed med bestillingsId={} til topic={}", varselId, KAFKA_TOPIC_VARSEL_MIN_SIDE);
		kafkaEventProducer.publish(KAFKA_TOPIC_VARSEL_MIN_SIDE, varselId, opprettVarselJson);
	}
}
