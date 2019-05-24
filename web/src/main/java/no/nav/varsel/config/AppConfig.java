package no.nav.varsel.config;

import com.codahale.metrics.servlets.MetricsServlet;
import no.nav.varsel.config.local.LocalTomcatConfiguration;
import no.nav.varsel.web.metrics.MetricsServletContextListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Spring configuration for the application
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({LocalTomcatConfiguration.class,
		ServiceConfig.class,
		JmsConsumerConfig.class,
		ProviderWsConfig.class,
		SelftestConfig.class,
		BatchConfig.class,
		WebConfig.class
})
@EnableRetry
public class AppConfig {

	@Bean
	public MetricsServlet.ContextListener metricContextListener() {
		return new MetricsServletContextListener();
	}

}
