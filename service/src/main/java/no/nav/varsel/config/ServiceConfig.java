package no.nav.varsel.config;

import no.nav.varsel.service.AktoerService;
import no.nav.varsel.service.BestillVarselService;
import no.nav.varsel.service.BrukervarselV1Service;
import no.nav.varsel.service.MottaVarselKvitteringService;
import no.nav.varsel.service.PingService;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.StoppReVarselService;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.support.DefaultBrukervarselV1Service;
import no.nav.varsel.service.support.DefaultPingService;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for Service
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({MetricsConfig.class, RepoConfig.class, WsConsumerConfig.class, JmsProducerConfig.class})
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
	public VarselutsendingToMapper varselutsendingToMapper() {
		return new VarselutsendingToMapper();
	}

	@Bean
	public MottaVarselKvitteringService mottaVarselKvitteringService() {
		return new MottaVarselKvitteringService();
	}

	@Bean
	public AktoerService aktoerService() {
		return new AktoerService();
	}

	@Bean
	public BestillVarselService bestillVarselService() {
		return new BestillVarselService();
	}

	@Bean
	public StoppReVarselService stoppReVarselService() {
		return new StoppReVarselService();
	}

	@Bean
	public PingService pingService() {
		return new DefaultPingService();
	}

	@Bean
	public BrukervarselV1Service brukervarselV1Service() {
		return new DefaultBrukervarselV1Service();
	}
}
