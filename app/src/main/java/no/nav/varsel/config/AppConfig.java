package no.nav.varsel.config;

import no.nav.varsel.config.alias.MqGatewayProperties;
import no.nav.varsel.nais.NaisContract;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for the application
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableConfigurationProperties({
		MqGatewayProperties.class
})
@EnableAutoConfiguration
@Import({
		NaisContract.class,
		ServiceConfig.class,
		JmsConsumerConfig.class,
		ProviderWsConfig.class,
		RetryLoggingInterceptor.class
})
public class AppConfig {

}
