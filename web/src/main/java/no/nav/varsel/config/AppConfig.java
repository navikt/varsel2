package no.nav.varsel.config;

import com.codahale.metrics.servlets.MetricsServlet;
import no.nav.varsel.config.local.LocalTomcatConfiguration;
import no.nav.varsel.web.metrics.MetricsServletContextListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for the application
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({LocalTomcatConfiguration.class,
		ServiceConfig.class,
		JmsConsumerConfig.class,
		BatchConfig.class,
		SelftestConfig.class,
		WebConfig.class
})
public class AppConfig {

	@Bean
	public MetricsServlet.ContextListener metricContextListener() {
		return new MetricsServletContextListener();
	}

}
