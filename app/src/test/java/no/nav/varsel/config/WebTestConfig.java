package no.nav.varsel.config;

import no.nav.varsel.Application;
import no.nav.varsel.repo.config.RepoTestConfig;
import no.nav.varsel.tvarsel001.jms.config.JmsTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({JmsTestConfig.class, RepoTestConfig.class, Application.class})
@Configuration
public class WebTestConfig {

}