package no.nav.varsel.kvarsel001;

import no.nav.varsel.repo.config.RepoConfig;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.ContainerCustomizer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAutoConfiguration
@Import({NotifikasjonStatusConsumer.class, RepoConfig.class})
public class RepoTestConfig {
	@Bean
	public DefaultErrorHandler spyableExponentialBackoffErrorhandler() {
		var exponentialBackoffErrorhandler = new DefaultErrorHandler(new ExponentialBackOff(500, 1.5));
		return Mockito.spy(exponentialBackoffErrorhandler);
	}

	/*
	//TODO: Tryner på circle reference om denne er med. Ser ikke ut til å savnes om man fjerner den?
	@Bean
	public <K, V> ContainerCustomizer<K, V, KafkaMessageListenerContainer<K, V>> defaultKafkaMessageListenerCustomizer(
			ConcurrentKafkaListenerContainerFactory<?, ?> listenerContainerFactory,
			DefaultErrorHandler spyableExponentialBackoffErrorhandler
	) {

		// Sett errorhandler på factoryen som spring bruker
		listenerContainerFactory.setCommonErrorHandler(spyableExponentialBackoffErrorhandler);

		// Lag en customizer (spring bruker tilsynelatende ikke denne likevel?)
		ContainerCustomizer<K, V, KafkaMessageListenerContainer<K, V>> customizer = listenerContainer ->
				listenerContainer.setCommonErrorHandler(
						spyableExponentialBackoffErrorhandler
				);

		return customizer;
	}*/

}
