package no.nav.varsel.kvarsel001;

import no.nav.varsel.repo.config.RepoConfig;
import org.h2.jdbcx.JdbcDataSource;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAutoConfiguration
@Import({NotifikasjonStatusConsumer.class, RepoConfig.class})
public class RepoTestConfig {

	@Bean
	@Primary
	public DataSource dataSource() {
		JdbcDataSource jdbcDataSource = new JdbcDataSource();
		jdbcDataSource.setUrl("jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1;NON_KEYWORDS=KEY,VALUE");
		return jdbcDataSource;
	}

	@Bean
	public DefaultErrorHandler spyableExponentialBackoffErrorhandler() {
		var exponentialBackoffErrorhandler = new DefaultErrorHandler(new ExponentialBackOff(500, 1.5));
		return Mockito.spy(exponentialBackoffErrorhandler);
	}
}
