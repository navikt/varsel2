package no.nav.varsel.repo.config;

import no.nav.varsel.kafka.CustomKafkaTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAutoConfiguration
@Import({RepoConfig.class, CustomKafkaTemplate.class})
public class RepoTestConfig {

}
