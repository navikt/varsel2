package no.nav.varsel.tvarsel006.itest;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Getter
@Setter
public class KafkaTestConsumer {

	private CountDownLatch latch = new CountDownLatch(1);
	private ConsumerRecord<?, ?> consumerRecord;

	@KafkaListener(topics = "${varsel.doknotifikasjon.kontakt.info.topic}")
	public void receive(ConsumerRecord<?, ?> consumerRecord) {
		setConsumerRecord(consumerRecord);
		latch.countDown();
	}
}
