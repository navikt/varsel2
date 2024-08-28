package no.nav.varsel.config;

import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.config.alias.ListenerProperties;
import no.nav.varsel.config.alias.MqGatewayProperties;
import no.nav.varsel.consumer.config.WebClientConfig;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.kvarsel001.NotifikasjonStatusConsumer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({
		MqGatewayProperties.class,
		ListenerProperties.class,
		AzureProperties.class,
		DataSourceAdditionalProperties.class,
		VarselProperties.class
})
@EnableAutoConfiguration
@Import({
		ServiceConfig.class,
		JmsConsumerConfig.class,
		WebClientConfig.class,
		RetryLoggingInterceptor.class,
		CustomKafkaTemplate.class,
		NotifikasjonStatusConsumer.class
})
public class AppConfig {

}
