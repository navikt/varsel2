package no.nav.varsel.config;

import no.nav.varsel.tvarsel006.NotifikasjonMedKontaktinfoPublisher;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test Config for JMS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableAutoConfiguration
@Import({JmsTestConfig.class, RepoTestConfig.class, ProviderWsConfig.class, STSTestConfig.class, NotifikasjonMedKontaktinfoPublisher.class})
@Configuration
public class WsProviderTestConfig {

}
