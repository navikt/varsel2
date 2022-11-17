package no.nav.varsel.config;

import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.repo.config.RepoTestConfig;
import no.nav.varsel.tvarsel006.NotifikasjonMedKontaktinfoPublisher;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test Config for JMS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableAutoConfiguration
@Import({
		JmsTestConfig.class,
		RepoTestConfig.class,
		ProviderWsConfig.class,
		NotifikasjonMedKontaktinfoPublisher.class,
		CustomKafkaTemplate.class,
		HentDigitalKontaktinformasjonConsumer.class,
		VarselKanalDecider.class,
		HentDigitalKontaktinformasjonMapper.class,
		VarselInfoConsumer.class
})
@Configuration
public class WsProviderTestConfig {

	@Bean
	public TokenConsumer tokenConsumer() {
		return (String s) -> new TokenResponse();
	}

	@Bean
	public AzureProperties azureProperties() {
		AzureProperties azureproperties = new AzureProperties();
		azureproperties.setAppScopeDigdirKrr("scope");
		azureproperties.setAppClientId("clientId");
		azureproperties.setAppClientSecret("secret");
		azureproperties.setOpenidConfigTokenEndpoint("url");
		return azureproperties;
	}

}
