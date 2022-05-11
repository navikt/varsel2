package no.nav.varsel.tvarsel001.itest;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Data
public class BrukernotifikasjonTestConsumer {

	private CountDownLatch latch = new CountDownLatch(1);
	private ConsumerRecord<?, ?> consumerRecord;

	@KafkaListener(
			topics = "${brukernotifikasjon.beskjed.topic}",
			properties = {"key.deserializer=io.confluent.kafka.serializers.KafkaAvroDeserializer"}
	)
	public void receive(ConsumerRecord<?, ?> consumerRecord) {
		setConsumerRecord(consumerRecord);
		latch.countDown();
	}
}
