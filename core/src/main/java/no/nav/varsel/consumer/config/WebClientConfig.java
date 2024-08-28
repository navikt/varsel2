package no.nav.varsel.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static java.time.Duration.ofSeconds;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient webClient() {
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(ofSeconds(20))
				.proxyWithSystemProperties();

		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}
}