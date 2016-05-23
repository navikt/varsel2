package no.nav.varsel.jms.consumer.config;

import no.nav.varsel.jms.consumer.config.tvarsel001.BestillServicemeldingConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for JMS Consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import(BestillServicemeldingConsumer.class)
@Configuration
public class ConsumerConfig {
}
