package no.nav.varsel.repo.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAutoConfiguration
@Import({RepoConfig.class, CustomKafkaTemplateTest.class})
public class RepoTestConfig {

}
