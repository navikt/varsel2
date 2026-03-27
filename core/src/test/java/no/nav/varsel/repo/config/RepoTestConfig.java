package no.nav.varsel.repo.config;

import no.nav.varsel.config.DataSourceAdditionalProperties;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, DataSourceAdditionalProperties.class})
@EnableAutoConfiguration
@Import({RepoConfig.class, CustomKafkaTemplate.class})
public class RepoTestConfig {

	///  I stedet for embedded DataSource fra `@AutoConfigureTestDatabase`
	///
	/// * `DB_CLOSE_DELAY`: hindrer at H2 dropper databasen hvis en kobling lukkes
	/// * `NON_KEYWORDS`: tabellen `FLETTE_PARAMETER` har reserverte keywords som kolonnenavn
	@Bean
	@Primary
	public DataSource dataSource() {
		JdbcDataSource jdbcDataSource = new JdbcDataSource();
		jdbcDataSource.setUrl("jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1;NON_KEYWORDS=KEY,VALUE");
		return jdbcDataSource;
	}

}
