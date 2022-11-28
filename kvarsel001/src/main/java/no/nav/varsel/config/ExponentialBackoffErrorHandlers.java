package no.nav.varsel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.ContainerCustomizer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class ExponentialBackoffErrorHandlers {

	@Bean
	public <K, V> ContainerCustomizer<K, V, KafkaMessageListenerContainer<K, V>> defaultKafkaMessageListenerCustomizer(
			ConcurrentKafkaListenerContainerFactory<?, ?> listenerContainerFactory
	) {
		var exponentialBackoffErrorhandler = new DefaultErrorHandler(new ExponentialBackOff(500, 1.5));

		// Sett errorhandler på factoryen som spring bruker
		listenerContainerFactory.setCommonErrorHandler(exponentialBackoffErrorhandler);

		// Lag en customizer (spring bruker tilsynelatende ikke denne likevel?)
		ContainerCustomizer<K, V, KafkaMessageListenerContainer<K, V>> customizer = listenerContainer ->
				listenerContainer.setCommonErrorHandler(
						exponentialBackoffErrorhandler
				);

		return customizer;
	}
}
