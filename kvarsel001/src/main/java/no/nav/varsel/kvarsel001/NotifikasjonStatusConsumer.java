package no.nav.varsel.kvarsel001;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.DoknotifikasjonStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifikasjonStatusConsumer {

	private static final String KAFKA_TOPIC_DOK_NOTIFIKASJON_STATUS = "teamdokumenthandtering.aapen-dok-notifikasjon-status";

	private final ObjectMapper objectMapper;

	public NotifikasjonStatusConsumer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		log.info("no.nav.varsel.kvarsel001.NotifikasjonStatusConsumer created!");
	}

	@KafkaListener(
			topics = KAFKA_TOPIC_DOK_NOTIFIKASJON_STATUS,
			groupId = "varsel-kvarsel001"
	)
	public void onMessage(final ConsumerRecord<String, Object> record) {

		log.info("Innkommende kafka record til topic={}, partition={}, offset={}", record.topic(), record.partition(), record.offset());

		try {
			DoknotifikasjonStatus doknotifikasjonStatus = objectMapper.readValue(record.value().toString(), DoknotifikasjonStatus.class);

			log.info("DoknotifikasjonStatus: bestillingsId={}, bestillerId={}, status={}, melding={}, distribusjonId={}",
					doknotifikasjonStatus.getBestillingsId(),
					doknotifikasjonStatus.getBestillerId(),
					doknotifikasjonStatus.getStatus(),
					doknotifikasjonStatus.getMelding(),
					doknotifikasjonStatus.getDistribusjonId());

		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException", e);
		} catch (Exception e) {
			log.error("Ukjent teknisk feil for knot004 (status). Konsumerer hendelse på nytt. Dette må følges opp.", e);
		}
	}
}
