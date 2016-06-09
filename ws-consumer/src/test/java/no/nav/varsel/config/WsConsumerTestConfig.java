package no.nav.varsel.config;


import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.varsel.mock.AktoerV2Mock;
import no.nav.varsel.mock.DigitalKontaktinformasjonV1Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.xml.ws.Endpoint;

/**
 * Test config for Ws Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({WsConsumerConfig.class, RestMock.class})
@Configuration
public class WsConsumerTestConfig {

	@Value("${aktoerv2.ws.url}")
	private String aktoerUrl;

	@Value("${dkif.ws.url}")
	private String dkifUrl;

	@Bean(destroyMethod = "stop")
	public Endpoint aktoerV2MockEndpoint() {
		return Endpoint.publish(aktoerUrl, aktoerV2Mock());
	}

	@Bean(destroyMethod = "stop")
	public Endpoint digitalKontaktinformasjonV1MockEndpoint() {
		return Endpoint.publish(dkifUrl, digitalKontaktinformasjonV1Mock());
	}

	@Bean
	public AktoerV2 aktoerV2Mock() {
		return new AktoerV2Mock();
	}

	@Bean
	public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1Mock() {
		return new DigitalKontaktinformasjonV1Mock();
	}

}