package no.nav.varsel.config;

import no.nav.varsel.repo.config.RepoConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Repository Test Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAutoConfiguration
@Import(RepoConfig.class)
public class RepoTestConfig {

}
