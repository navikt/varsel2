package no.nav.varsel.config.endpoint;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Spring config for Dkif CXF endpoint
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class DkifEndpoint extends AbstractCxfEndpointConfig {

	@Value("${dkif.ws.url}")
	private String dkifUrl;

	@Bean
	public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1() throws IOException {
		addInterceptor(new SystemSAMLOutInterceptor());
		setAdress(dkifUrl);
		enableMtom();

		return createPort(DigitalKontaktinformasjonV1.class);
	}

}
