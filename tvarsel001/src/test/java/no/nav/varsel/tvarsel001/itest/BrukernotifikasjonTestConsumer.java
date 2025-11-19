package no.nav.varsel.tvarsel001.itest;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

import static no.nav.varsel.tvarsel001.BeskjedMinSidePublisher.KAFKA_TOPIC_VARSEL_MIN_SIDE;

@Component
@Data
public class BrukernotifikasjonTestConsumer {

	private CountDownLatch latch = new CountDownLatch(1);
	private ConsumerRecord<?, ?> consumerRecord;

	@KafkaListener(
			topics = KAFKA_TOPIC_VARSEL_MIN_SIDE,
			properties = {"key.deserializer=org.apache.kafka.common.serialization.StringDeserializer"}
	)
	public void receive(ConsumerRecord<?, ?> consumerRecord) {
		setConsumerRecord(consumerRecord);
		latch.countDown();
	}
}
