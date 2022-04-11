package no.nav.varsel.tvarsel006;

import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Import(KafkaEventProducer.class)
public class VarselUtsendelse {

	private final KafkaEventProducer kafkaEventProducer;
	private final String topic;

	public VarselUtsendelse(KafkaEventProducer kafkaEventProducer,
							@Value("${varsel.doknotifikasjon.kontakt.info.topic}") String topic) {
		this.kafkaEventProducer = kafkaEventProducer;
		this.topic = topic;
	}

	public void sendVarsel(NotifikasjonMedkontaktInfo notifikasjonMedkontaktInfo) {

		kafkaEventProducer.publish(
				topic,
				notifikasjonMedkontaktInfo.getBestillingsId(), //FIXME Blir key brukt til noe?
				notifikasjonMedkontaktInfo);
	}
}
