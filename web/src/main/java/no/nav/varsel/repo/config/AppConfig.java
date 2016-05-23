package no.nav.varsel.repo.config;

import no.nav.varsel.config.JmsConfig;
import no.nav.varsel.ServiceConfig;
import no.nav.varsel.repo.config.local.LocalTomcatConfiguration;
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
		JmsConfig.class,
		SelftestConfig.class
})
public class AppConfig {
}
