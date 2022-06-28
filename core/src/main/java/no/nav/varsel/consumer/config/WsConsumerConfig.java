package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import no.nav.varsel.ws.config.CxfConfig;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
		CxfConfig.class,
		ConsumerEndpointConfig.class,
		RestConsumerConfig.class,
		PdlIdentConsumer.class,
		StsRestConsumer.class
})
@Configuration
public class WsConsumerConfig {


	@Bean
	public VarselInfoConsumer varselInfoConsumer() {
		return new VarselInfoConsumer();
	}

	@Bean
	HttpClient httpClient(HttpClientConnectionManager connectionManager) {
		return HttpClients.custom()
				.setConnectionManager(connectionManager)
				.build();
	}

	@Bean
	HttpClientConnectionManager httpClientConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(400);
		connectionManager.setDefaultMaxPerRoute(100);
		return connectionManager;
	}

}
