package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PdlIdentConsumer.class})
public class ConsumerEndpointConfig {
}
