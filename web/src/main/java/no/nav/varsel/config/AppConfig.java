package no.nav.varsel.config;

import no.nav.varsel.ProviderConfig;
import no.nav.varsel.ServiceConfig;
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
		ProviderConfig.class,
		SelftestConfig.class
})
public class AppConfig {
}
