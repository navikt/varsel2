package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for Ws consumers endpoints
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({PdlIdentConsumer.class})
public class ConsumerEndpointConfig {
}
