package no.nav.varsel.config;

import no.nav.varsel.consumer.config.WsConsumerConfig;
import no.nav.varsel.repo.config.RepoConfig;
import no.nav.varsel.service.AktoerService;
import no.nav.varsel.service.BrukervarselV1Service;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.interfaces.BrukervarselService;
import no.nav.varsel.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.support.VarselutsendingMapper;
import no.nav.varsel.service.tvarsel001.support.BrukernotifikasjonMapper;
import no.nav.varsel.service.tvarsel001.support.NotifikasjonMapper;
import no.nav.varsel.service.tvarsel005.support.BrukervarselMapper;
import no.nav.varsel.service.tvarsel006.support.NotifikasjonMedKontaktinfoMapper;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import no.nav.varsel.tvarsel001.NotifikasjonPublisher;
import no.nav.varsel.tvarsel006.NotifikasjonMedKontaktinfoPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for Service
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({RepoConfig.class,
		WsConsumerConfig.class,
		JmsConfig.class,
		NotifikasjonPublisher.class,
		NotifikasjonMapper.class,
		NotifikasjonMedKontaktinfoPublisher.class,
		NotifikasjonMedKontaktinfoMapper.class,
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

	@Bean
	public BrukervarselService brukervarselV1Service() {
		return new BrukervarselV1Service();
	}

	@Bean
	public BrukervarselMapper brukervarselMapper() {
		return new BrukervarselMapper();
	}
}
