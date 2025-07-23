package no.nav.varsel.tvarsel001.jms.config;

import no.nav.varsel.config.NaisProperties;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dokmet.DokmetConsumer;
import no.nav.varsel.consumer.naistoken.NaisTexasConsumer;
import no.nav.varsel.consumer.naistoken.RestClientConfig;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.repo.config.RepoTestConfig;
import no.nav.varsel.tvarsel001.service.config.ServiceTestConfig;
import no.nav.varsel.tvarsel001.service.service.VarselFletter;
import no.nav.varsel.tvarsel001.service.service.support.VarselBestillingDomainMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Import({JmsTestConfig.class,
		RepoTestConfig.class,
		ServiceTestConfig.class,
		JmsConsumerConfig.class,
		CustomKafkaTemplate.class,
		HentDigitalKontaktinformasjonConsumer.class,
		VarselKanalDecider.class,
		HentDigitalKontaktinformasjonMapper.class,
		DokmetConsumer.class,
		NaisTexasConsumer.class,
		VarselBestillingDomainMapper.class,
		VarselFletter.class,
		RestClientConfig.class
})
@Configuration
public class JmsConsumerTestConfig {

	@Bean
	public NaisProperties naisProperties() {
		return new NaisProperties("url");
	}
	@Bean
	public ClientHttpRequestFactory requestFactory() {
		return new SimpleClientHttpRequestFactory();
	}

}