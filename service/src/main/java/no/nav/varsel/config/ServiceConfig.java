package no.nav.varsel.config;

import no.nav.varsel.jms.producer.config.JmsProducerConfig;
import no.nav.varsel.repo.config.RepoConfig;
import no.nav.varsel.service.MottaVarselKvitteringService;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.VarslelKanalDecider;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.tvarsel001.support.ServicemeldingDomainMapper;
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
	public ServicemeldingDomainMapper servicemeldingDomainMapper() {
		return new ServicemeldingDomainMapper();
	}

	@Bean
	public VarselFletter varselFletter() {
		return new VarselFletter();
	}

	@Bean
	public VarslelKanalDecider varslelKanalDecider() {
		return new VarslelKanalDecider();
	}

	@Bean
	public VarselutsendingToMapper varselutsendingToMapper() {
		return new VarselutsendingToMapper();
	}

	@Bean
	public MottaVarselKvitteringService mottaVarselKvitteringService() {
	 	return new MottaVarselKvitteringService();
	}
}
