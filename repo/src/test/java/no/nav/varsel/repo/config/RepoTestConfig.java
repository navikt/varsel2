package no.nav.varsel.repo.config;

import no.nav.varsel.repo.config.RepoConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Repository Test Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableAutoConfiguration
@Import(RepoConfig.class)
public class RepoTestConfig {
}
