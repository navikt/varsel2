package no.nav.varsel.config.endpoint;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.varsel.config.AbstractCxfEndpointConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring config for Aktoer v2 CXF endpoint
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class AktoerV2Endpoint extends AbstractCxfEndpointConfig {

	@Value("${aktoerv2.ws.url}")
	private String aktoerUrl;

	@Bean
	public AktoerV2 aktoerV2() {
		addOutInterceptor(new SystemSAMLOutInterceptor());
		setAdress(aktoerUrl);
		enableMtom();

		return createPort(AktoerV2.class);
	}

}
