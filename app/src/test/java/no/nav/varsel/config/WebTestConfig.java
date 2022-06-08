package no.nav.varsel.config;

import no.nav.varsel.Application;
import no.nav.varsel.consumer.config.STSTestConfig;
import no.nav.varsel.repo.config.RepoTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test config for App
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({JmsTestConfig.class, RepoTestConfig.class, STSTestConfig.class, Application.class})
@Configuration
public class WebTestConfig {

}