package no.nav.varsel.kafka;

import lombok.extern.slf4j.Slf4j;

import no.nav.varsel.exception.technical.KafkaTechnicalException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaEventProducer {

	private final KafkaTemplate<Object, Object> kafkaTemplate;

	KafkaEventProducer(KafkaTemplate<Object, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	//@Monitor(createErrorMetric = true, errorMetricInclude = KafkaTechnicalException.class)
	@Retryable(include = KafkaTechnicalException.class, backoff = @Backoff(delay = 2000))
	public void publish(String topic, Object key, Object event) {
		ProducerRecord<Object, Object> producerRecord = new ProducerRecord<>(
				topic,
				null,
				System.currentTimeMillis(),
				key,
				event
		);

		try {
			SendResult<Object, Object> sendResult = kafkaTemplate.send(producerRecord).get();
			log.info("Hendelse skrevet til topic. Timestamp={}, partition={}, topic={}",
					sendResult.getRecordMetadata().timestamp(),
					sendResult.getRecordMetadata().partition(),
					sendResult.getRecordMetadata().topic()
			);
		} catch (RetriableException e) {
			throw new KafkaTechnicalException(String.format("Failed to send message to kafka. Topic: %s", topic), e);
		} catch (ExecutionException | InterruptedException e) {
			//TODO Noen hensikt å retry her?
			e.printStackTrace();
		}
	}
}
