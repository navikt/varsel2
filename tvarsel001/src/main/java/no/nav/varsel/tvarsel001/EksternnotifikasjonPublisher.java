package no.nav.varsel.tvarsel001;

import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Import(KafkaEventProducer.class)
public class EksternnotifikasjonPublisher {

	private final KafkaEventProducer kafkaEventProducer;
	private final String topic;

	public EksternnotifikasjonPublisher(KafkaEventProducer kafkaEventProducer,
										@Value("${varsel.doknotifikasjon.eksternnotifikasjon.topic}") String topic) {
		this.kafkaEventProducer = kafkaEventProducer;
		this.topic = topic;
	}

	public void sendNotifikasjon(Doknotifikasjon doknotifikasjon) {
		log.info("Sender eksternnotifikasjon med bestillingsId=" + doknotifikasjon.getBestillingsId());
		kafkaEventProducer.publish(topic, doknotifikasjon.getBestillingsId(), doknotifikasjon);
	}
}
