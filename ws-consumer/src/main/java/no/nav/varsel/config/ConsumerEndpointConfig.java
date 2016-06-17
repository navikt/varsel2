package no.nav.varsel.config;

import no.nav.varsel.config.endpoint.AktoerV2Endpoint;
import no.nav.varsel.config.endpoint.DkifEndpoint;
import no.nav.varsel.config.endpoint.KodeverkV2Endpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for Ws consumers endpoints
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({AktoerV2Endpoint.class, DkifEndpoint.class, KodeverkV2Endpoint.class})
public class ConsumerEndpointConfig {
}
