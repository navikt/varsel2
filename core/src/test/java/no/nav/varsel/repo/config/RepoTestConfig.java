package no.nav.varsel.repo.config;

import no.nav.varsel.config.DataSourceAdditionalProperties;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import io.github.resilience4j.springboot3.verifier.autoconfigure.SpringBoot3VerifierAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, DataSourceAdditionalProperties.class})
@EnableAutoConfiguration(exclude = SpringBoot3VerifierAutoConfiguration.class)
@Import({RepoConfig.class, CustomKafkaTemplate.class, H2TestDataSourceConfig.class})
public class RepoTestConfig {

}
