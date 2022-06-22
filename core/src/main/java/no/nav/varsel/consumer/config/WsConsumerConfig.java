package no.nav.varsel.consumer.config;

import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.azure.AzureTokenConsumer;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import no.nav.varsel.consumer.support.VarselKanalDecider;
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
		StsRestConsumer.class,
		HentDigitalKontaktinformasjonConsumer.class,
		AzureTokenConsumer.class,
		AzureProperties.class,
		VarselProperties.class
})
@Configuration
public class WsConsumerConfig {

	@Bean
	public HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper() {
		return new HentDigitalKontaktinformasjonMapper();
	}

	@Bean
	public VarselInfoConsumer varselInfoConsumer() {
		return new VarselInfoConsumer();
	}

	@Bean
	public VarselKanalDecider varselKanalDecider() {
		return new VarselKanalDecider();
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
