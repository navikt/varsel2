package no.nav.varsel.config.endpoint;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.varsel.config.AbstractCxfEndpointConfig;
import no.nav.varsel.config.STSConfig;
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

    @Value("${aktoerv2.ws.endpointUrl}")
    private String aktoerUrl;

    @Bean
    public AktoerV2 aktoerV2(STSConfig stsConfig) {
        setAdress(aktoerUrl);
        enableMtom();
        AktoerV2 port = createPort(AktoerV2.class);
        stsConfig.configureSTS(port);
        return port;
    }

}
