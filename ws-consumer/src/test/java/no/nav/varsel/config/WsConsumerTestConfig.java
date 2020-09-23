package no.nav.varsel.config;


import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.varsel.config.local.LocalTomcatConfiguration;
import no.nav.varsel.mock.AktoerV2Mock;
import no.nav.varsel.mock.DigitalKontaktinformasjonV1Mock;
import no.nav.varsel.mock.KodeverkPortTypeMock;
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
@Import({LocalTomcatConfiguration.class, WsConsumerConfig.class, RestMock.class, STSTestConfig.class})
@Configuration
public class WsConsumerTestConfig {

	@Value("${aktoerv2.ws.endpointUrl}")
	private String aktoerUrl;

	@Value("${dkif.ws.endpointUrl}")
	private String dkifUrl;

	@Value("${kodeverkv2.ws.endpointUrl}")
	private String kodeverkUrl;


	@Bean(destroyMethod = "stop")
	public Endpoint aktoerV2MockEndpoint() {
		return Endpoint.publish(aktoerUrl, aktoerV2Mock());
	}

	@Bean(destroyMethod = "stop")
	public Endpoint digitalKontaktinformasjonV1MockEndpoint() {
		return Endpoint.publish(dkifUrl, digitalKontaktinformasjonV1Mock());
	}

	@Bean(destroyMethod = "stop")
	public Endpoint kodeverkPortTypeMockEndpoint() {
		return Endpoint.publish(kodeverkUrl, kodeverkPortTypeMock());
	}


	@Bean
	public AktoerV2 aktoerV2Mock() {
		return new AktoerV2Mock();
	}

	@Bean
	public KodeverkPortType kodeverkPortTypeMock() {
		return new KodeverkPortTypeMock();
	}

	@Bean
	public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1Mock() {
		return new DigitalKontaktinformasjonV1Mock();
	}
}