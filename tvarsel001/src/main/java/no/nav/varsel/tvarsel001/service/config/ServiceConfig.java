package no.nav.varsel.tvarsel001.service.config;

import no.nav.varsel.consumer.config.WsConsumerConfig;
import no.nav.varsel.repo.config.RepoConfig;
import no.nav.varsel.tvarsel001.BeskjedMinSidePublisher;
import no.nav.varsel.tvarsel001.jms.config.JmsConfig;
import no.nav.varsel.tvarsel001.service.service.AktoerService;
import no.nav.varsel.tvarsel001.service.service.ServicemeldingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Clock;

@Configuration
@Import({RepoConfig.class,
		WsConsumerConfig.class,
		JmsConfig.class,
		BeskjedMinSidePublisher.class,
		ServicemeldingService.class
})
public class ServiceConfig {

	@Bean
	public AktoerService aktoerService() {
		return new AktoerService();
	}

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}
}
