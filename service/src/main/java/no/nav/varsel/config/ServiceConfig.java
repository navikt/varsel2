package no.nav.varsel.config;

import no.nav.varsel.consumer.config.WsConsumerConfig;
import no.nav.varsel.repo.config.RepoConfig;
import no.nav.varsel.service.AktoerService;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.support.VarselutsendingMapper;
import no.nav.varsel.service.tvarsel001.support.BrukernotifikasjonMapper;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
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
	public VarselBestillingDomainMapper varselBestillingDomainMapper() {
		return new VarselBestillingDomainMapper();
	}

	@Bean
	public VarselFletter varselFletter() {
		return new VarselFletter();
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
