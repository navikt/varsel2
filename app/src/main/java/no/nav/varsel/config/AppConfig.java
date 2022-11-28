package no.nav.varsel.config;

import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.config.alias.ListenerProperties;
import no.nav.varsel.config.alias.MqGatewayProperties;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.kvarsel001.NotifikasjonStatusConsumer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for the application
 */
@Configuration
@EnableConfigurationProperties({
		MqGatewayProperties.class,
		ListenerProperties.class,
		VarselProperties.class,
		AzureProperties.class
})
@EnableAutoConfiguration
@Import({
		ServiceConfig.class,
		JmsConsumerConfig.class,
		ProviderWsConfig.class,
		RetryLoggingInterceptor.class,
		CustomKafkaTemplate.class,
		ExponentialBackoffErrorHandlers.class,
		NotifikasjonStatusConsumer.class
})
public class AppConfig {

}
