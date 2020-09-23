package no.nav.varsel.config.endpoint;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.varsel.config.AbstractCxfEndpointConfig;
import no.nav.varsel.config.STSConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring config for Dkif CXF endpoint
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class DkifEndpoint extends AbstractCxfEndpointConfig {

    @Value("${dkif.ws.endpointUrl}")
    private String dkifUrl;

    @Bean
    public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1(STSConfig stsConfig) {
        setAdress(dkifUrl);
        enableMtom();
        DigitalKontaktinformasjonV1 port = createPort(DigitalKontaktinformasjonV1.class);
        stsConfig.configureSTS(port);
        return port;
    }

}
