package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import no.nav.varsel.ws.config.CxfConfig;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

@Import({
		CxfConfig.class,
		ConsumerEndpointConfig.class,
		RestConsumerConfig.class,
		PdlIdentConsumer.class,
		StsRestConsumer.class
})
@Configuration
public class WsConsumerConfig {

	public static final Duration READ_TIMEOUT = Duration.ofSeconds(20);

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
}
