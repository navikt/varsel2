package no.nav.varsel.consumer.config;

import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.dokmet.VarselinfoMapper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestConsumerConfig {

	public static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

	@Bean
	public RestTemplate restTemplate(
			VarselProperties varselProperties,
			RestTemplateBuilder restTemplateBuilder,
			ClientHttpRequestFactory clientHttpRequestFactory) {
		return restTemplateBuilder
				.requestFactory(() -> clientHttpRequestFactory)
				.basicAuthentication(varselProperties.getServiceuser().getUsername(), varselProperties.getServiceuser().getPassword())
				.build();
	}

	@Bean
	public ClientHttpRequestFactory requestFactory(HttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	@Bean
	public VarselinfoMapper varselInfoMapper() {
		return new VarselinfoMapper();
	}

}
