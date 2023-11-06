package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.dokkat.support.VarselInfoMapper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestConsumerConfig {

	public static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
	public static final Duration READ_TIMEOUT = Duration.ofSeconds(20);


	@Value("${varsel.serviceuser.username}")
	private String srvVarselUsername;
	@Value("${varsel.serviceuser.password}")
	private String srvVarselPassword;


	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
									 ClientHttpRequestFactory clientHttpRequestFactory) {
		return restTemplateBuilder
				.requestFactory(() -> clientHttpRequestFactory)
				.setConnectTimeout(CONNECT_TIMEOUT)
				.basicAuthentication(srvVarselUsername, srvVarselPassword).build();
	}

	@Bean
	public ClientHttpRequestFactory requestFactory(HttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	@Bean
	public HttpClient httpClient(SocketConfig socketConfig) {
		PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
				.setMaxConnPerRoute(100)
				.setMaxConnTotal(400)
				.setDefaultSocketConfig(socketConfig)
				.build();
		return HttpClients.custom()
				.setConnectionManager(connectionManager)
				.build();
	}

	@Bean
	HttpClientConnectionManager httpClientConnectionManager(SocketConfig socketConfig) {
		return PoolingHttpClientConnectionManagerBuilder.create()
				.setDefaultSocketConfig(socketConfig)
				.setMaxConnPerRoute(100)
				.setMaxConnTotal(400)
				.build();
	}

	@Bean
	public SocketConfig socketConfig() {
		return SocketConfig.custom()
				.setSoTimeout(Timeout.of(READ_TIMEOUT))
				.build();
	}

	@Bean
	public VarselInfoMapper varselInfoMapper() {
		return new VarselInfoMapper();
	}

}
