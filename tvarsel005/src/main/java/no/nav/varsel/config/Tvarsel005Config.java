package no.nav.varsel.config;

import no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint;
import no.nav.varsel.provider.ws.brukervarsel.support.BrukervarselV1Provider;
import no.nav.varsel.provider.ws.brukervarsel.support.HentVarselForBrukerRequestMapper;
import no.nav.varsel.provider.ws.brukervarsel.support.HentVarselForBrukerRequestValidator;
import no.nav.varsel.provider.ws.brukervarsel.support.HentVarselForBrukerResponseMapper;
import no.nav.varsel.provider.ws.brukervarsel.support.VarselMapper;
import no.nav.varsel.provider.ws.brukervarsel.support.VarselbestillingMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tvarsel005Config {

	@Bean
	public BrukervarselV1Endpoint brukervarselV1Endpoint() {
		return new BrukervarselV1Endpoint();
	}

	@Bean
	public BrukervarselV1Provider brukervarselV1Provider() {
		return new BrukervarselV1Provider();
	}

	@Bean
	public HentVarselForBrukerRequestValidator hentVarselForBrukerRequestValidator() {
		return new HentVarselForBrukerRequestValidator();
	}

	@Bean
	public HentVarselForBrukerResponseMapper hentVarselForBrukerResponseMapper() {
		return new HentVarselForBrukerResponseMapper();
	}

	@Bean
	public HentVarselForBrukerRequestMapper hentVarselForBrukerRequestMapper() {
		return new HentVarselForBrukerRequestMapper();
	}

	@Bean
	public VarselbestillingMapper varselbestillingMapper() {
		return new VarselbestillingMapper();
	}

	@Bean
	public VarselMapper varselMapper() {
		return new VarselMapper();
	}
}
