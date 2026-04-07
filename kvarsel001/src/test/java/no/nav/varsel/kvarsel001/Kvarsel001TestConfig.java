package no.nav.varsel.kvarsel001;

import no.nav.varsel.repo.config.H2TestDataSourceConfig;
import no.nav.varsel.repo.config.RepoConfig;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAutoConfiguration
@Import({NotifikasjonStatusConsumer.class, RepoConfig.class, H2TestDataSourceConfig.class})
public class Kvarsel001TestConfig {

	@Bean
	public DefaultErrorHandler spyableExponentialBackoffErrorhandler() {
		var exponentialBackoffErrorhandler = new DefaultErrorHandler(new ExponentialBackOff(500, 1.5));
		return Mockito.spy(exponentialBackoffErrorhandler);
	}
}
