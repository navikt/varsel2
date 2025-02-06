package no.nav.varsel.tvarsel001.jms.config;

import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.tvarsel001.service.config.ServiceTestConfig;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.config.WebClientConfig;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dokmet.DokmetConsumer;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.repo.config.RepoTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Import({JmsTestConfig.class,
		RepoTestConfig.class,
		ServiceTestConfig.class,
		JmsConsumerConfig.class,
		WebClientConfig.class,
		CustomKafkaTemplate.class,
		HentDigitalKontaktinformasjonConsumer.class,
		VarselKanalDecider.class,
		HentDigitalKontaktinformasjonMapper.class,
		DokmetConsumer.class
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
		azureproperties.setAppScopeDigdirKrr("scope");
		azureproperties.setAppClientId("clientId");
		azureproperties.setAppClientSecret("secret");
		azureproperties.setOpenidConfigTokenEndpoint("url");
		return azureproperties;
	}

	@Bean
	public ClientHttpRequestFactory requestFactory() {
		return new SimpleClientHttpRequestFactory();
	}

	@Bean
	public VarselProperties varselProperties() {
		VarselProperties varselProperties = new VarselProperties();
		varselProperties.getEndpoints().setDokmetUrl("dokmeturl");
		return varselProperties;
	}

}