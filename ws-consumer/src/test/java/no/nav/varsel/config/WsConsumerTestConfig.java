package no.nav.varsel.config;


import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.varsel.config.cache.LokalCacheConfig;
import no.nav.varsel.config.local.LocalTomcatConfiguration;
import no.nav.varsel.mock.DigitalKontaktinformasjonV1Mock;
import no.nav.varsel.wsconsumer.pdl.PdlIdentConsumer;
import no.nav.varsel.wsconsumer.sts.StsRestConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.xml.ws.Endpoint;

@Import({
		LocalTomcatConfiguration.class,
		WsConsumerConfig.class,
		STSTestConfig.class,
		PdlIdentConsumer.class,
		StsRestConsumer.class,
		LokalCacheConfig.class
})
@Configuration
public class WsConsumerTestConfig {

	@Value("${dkif.ws.endpointUrl}")
	private String dkifUrl;

	@Bean(destroyMethod = "stop")
	public Endpoint digitalKontaktinformasjonV1MockEndpoint() {
		return Endpoint.publish(dkifUrl, digitalKontaktinformasjonV1Mock());
	}

	@Bean
	public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1Mock() {
		return new DigitalKontaktinformasjonV1Mock();
	}
}