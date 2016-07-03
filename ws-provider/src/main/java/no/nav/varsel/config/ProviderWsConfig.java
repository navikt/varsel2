package no.nav.varsel.config;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.provider.map.HentVarselForBrukerRequestMapper;
import no.nav.varsel.provider.map.HentVarselForBrukerResponseMapper;
import no.nav.varsel.provider.map.VarselMapper;
import no.nav.varsel.provider.map.VarselbestillingMapper;
import no.nav.varsel.provider.map.support.DefaultHentVarselForBrukerRequestMapper;
import no.nav.varsel.provider.map.support.DefaultHentVarselForBrukerResponseMapper;
import no.nav.varsel.provider.map.support.DefaultVarselMapper;
import no.nav.varsel.provider.map.support.DefaultVarselbestillingMapper;
import no.nav.varsel.provider.map.support.HentVarselForBrukerRequestValidator;
import no.nav.varsel.provider.ws.handler.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for Web Service Provider.
 *
 * @author Lars Aune
 */
@Configuration
@Import(ServiceConfig.class)
public class ProviderWsConfig {
	@Bean
	public Validator<HentVarselForBrukerRequest> hentVarselForBrukerRequestValidator() {
		return new HentVarselForBrukerRequestValidator();
	}

	@Bean
	public HentVarselForBrukerResponseMapper hentVarselForBrukerResponseMapper() {
		return new DefaultHentVarselForBrukerResponseMapper();
	}

	@Bean
	public HentVarselForBrukerRequestMapper hentVarselForBrukerRequestMapper() {
		return new DefaultHentVarselForBrukerRequestMapper();
	}

	@Bean
	public VarselbestillingMapper varselbestillingMapper() {
		return new DefaultVarselbestillingMapper();
	}

	@Bean
	public VarselMapper varselMapper() {
		return new DefaultVarselMapper();
	}
}
