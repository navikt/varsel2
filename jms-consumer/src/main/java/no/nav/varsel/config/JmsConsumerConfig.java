package no.nav.varsel.config;

import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for JMS Consumers
 */
@Import({ServiceConfig.class, JmsConfig.class, ShutdownHook.class,
		BestillServicemeldingConsumer.class})
@Configuration
public class JmsConsumerConfig {

}
