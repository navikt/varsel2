package no.nav.varsel.config;

import no.nav.varsel.config.local.LocalTomcatConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for the application
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({LocalTomcatConfiguration.class,
		MetricsConfig.class,
		ServiceConfig.class,
		JmsConsumerConfig.class,
		SelftestConfig.class
})
public class AppConfig {
}
