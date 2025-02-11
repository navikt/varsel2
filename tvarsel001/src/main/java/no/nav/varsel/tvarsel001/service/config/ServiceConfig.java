package no.nav.varsel.tvarsel001.service.config;

import no.nav.varsel.consumer.config.WsConsumerConfig;
import no.nav.varsel.repo.config.RepoConfig;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import no.nav.varsel.tvarsel001.jms.config.JmsConfig;
import no.nav.varsel.tvarsel001.service.service.AktoerService;
import no.nav.varsel.tvarsel001.service.service.ServicemeldingService;
import no.nav.varsel.tvarsel001.service.service.support.BrukernotifikasjonMapper;
import no.nav.varsel.tvarsel001.service.service.support.VarselutsendingMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RepoConfig.class,
		WsConsumerConfig.class,
		JmsConfig.class,
		BrukernotifikasjonBeskjedPublisher.class,
		BrukernotifikasjonMapper.class
})
public class ServiceConfig {

	@Bean
	public ServicemeldingService servicemeldingService() {
		return new ServicemeldingService();
	}

	@Bean
	public VarselutsendingMapper varselutsendingMapper() {
		return new VarselutsendingMapper();
	}

	@Bean
	public AktoerService aktoerService() {
		return new AktoerService();
	}

}
