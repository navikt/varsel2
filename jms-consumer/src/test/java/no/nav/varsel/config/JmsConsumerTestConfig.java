package no.nav.varsel.config;

import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.azure.digdir.AzureProperties;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.repo.config.RepoTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test Config for JMS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({JmsTestConfig.class, RepoTestConfig.class,
		ServiceTestConfig.class, JmsConsumerConfig.class,
		CustomKafkaTemplate.class,
		HentDigitalKontaktinformasjonConsumer.class,
		VarselKanalDecider.class,
		HentDigitalKontaktinformasjonMapper.class
})
@Configuration
public class JmsConsumerTestConfig {

	@Bean
	public TokenConsumer tokenConsumer() {
		return (String s) -> new TokenResponse();
	}

	@Bean
	public AzureProperties azureProperties() {
		AzureProperties azureproperties = new AzureProperties();
		azureproperties.setScopeDigdirKrr("scope");
		azureproperties.setClientId("clientId");
		azureproperties.setClientSecret("secret");
		azureproperties.setTenantId("tenantId");
		azureproperties.setTokenUrl("url");
		azureproperties.setWellKnownUrl("wellKnown");
		return azureproperties;
	}

}
