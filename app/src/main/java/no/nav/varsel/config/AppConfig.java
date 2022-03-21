package no.nav.varsel.config;

import com.codahale.metrics.servlets.MetricsServlet;
import no.nav.varsel.config.alias.MqGatewayProperties;
import no.nav.varsel.nais.NaisContract;
import no.nav.varsel.web.metrics.MetricsServletContextListener;
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
		SelftestConfig.class,
		RetryLoggingInterceptor.class
})
public class AppConfig {

	@Bean
	public MetricsServlet.ContextListener metricContextListener() {
		return new MetricsServletContextListener();
	}

}
