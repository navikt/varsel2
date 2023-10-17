package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import no.nav.varsel.ws.config.CxfConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
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
	HttpClientConnectionManager httpClientConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(400);
		connectionManager.setDefaultMaxPerRoute(100);
		return connectionManager;
	}

}
