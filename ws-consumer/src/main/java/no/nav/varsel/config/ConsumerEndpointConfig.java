package no.nav.varsel.config;

import no.nav.varsel.config.endpoint.DkifEndpoint;
import no.nav.varsel.wsconsumer.pdl.PdlIdentConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for Ws consumers endpoints
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({PdlIdentConsumer.class, DkifEndpoint.class})
public class ConsumerEndpointConfig {
}
