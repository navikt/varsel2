package no.nav.varsel.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Repository Test Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableAutoConfiguration(exclude = DataSourceTransactionManagerAutoConfiguration.class)
@Import(RepoConfig.class)
public class RepoTestConfig {

}
