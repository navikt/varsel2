package no.nav.varsel.config;

import no.nav.varsel.service.AktoerService;
import no.nav.varsel.service.MottaVarselKvitteringService;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.BestillVarselService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for Service
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({RepoConfig.class, WsConsumerConfig.class, JmsProducerConfig.class})
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
}
