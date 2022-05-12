package no.nav.varsel.kafka;

import lombok.extern.slf4j.Slf4j;

import no.nav.varsel.exception.technical.KafkaTechnicalException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaEventProducer {

	private static final String KAFKA_NOT_AUTHENTICATED = "Not authenticated to publish to topic: ";
	private static final String KAFKA_FAILED_TO_SEND = "Failed to send message to kafka. Topic: ";

	private final RoutingKafkaTemplate routingKafkaTemplate;

	public KafkaEventProducer(RoutingKafkaTemplate routingKafkaTemplate) {
		this.routingKafkaTemplate = routingKafkaTemplate;
	}

	@Retryable(include = KafkaTechnicalException.class, backoff = @Backoff(delay = 2000))
	public void publish(String topic, Object key, Object event) {
		ProducerRecord<Object, Object> producerRecord = new ProducerRecord<>(
				topic,
				null,
				System.currentTimeMillis(),
				key,
				event
		);

		log.info("ProducerRecord Key={} Value={}", producerRecord.key(), producerRecord.value());

		try {
			SendResult<Object, Object> sendResult = routingKafkaTemplate.send(producerRecord).get();
			log.info("Hendelse skrevet til topic. Timestamp={}, partition={}, topic={}",
					sendResult.getRecordMetadata().timestamp(),
					sendResult.getRecordMetadata().partition(),
					sendResult.getRecordMetadata().topic()
			);
		} catch (ExecutionException executionException) {
			if (executionException.getCause() instanceof KafkaProducerException kafkaProducerException) {
				if (kafkaProducerException.getCause() instanceof TopicAuthorizationException) {
					throw new KafkaTechnicalException(KAFKA_NOT_AUTHENTICATED + topic, kafkaProducerException.getCause());
				}
			}
			throw new KafkaTechnicalException(KAFKA_FAILED_TO_SEND + topic, executionException);
		} catch (InterruptedException | KafkaException e) {
			throw new KafkaTechnicalException(KAFKA_FAILED_TO_SEND + topic, e);
		}
	}
}
