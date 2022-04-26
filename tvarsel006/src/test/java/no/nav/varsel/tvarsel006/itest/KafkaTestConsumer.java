package no.nav.varsel.tvarsel006.itest;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Data
public class KafkaTestConsumer {

	private CountDownLatch latch = new CountDownLatch(1);
	private ConsumerRecord<?, ?> consumerRecord;

	@KafkaListener(topics = "${privat-dok-notifikasjon-med-kontakt-info.topic}")
	public void receive(ConsumerRecord<?, ?> consumerRecord) {
		setConsumerRecord(consumerRecord);
		latch.countDown();
	}
}
