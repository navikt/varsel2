package no.nav.varsel.config;

import no.nav.varsel.jms.consumer.JmsConsumerManager;
import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ServiceConfig.class,
		JmsConfig.class,
		ShutdownHook.class,
		BestillServicemeldingConsumer.class,
		JmsConsumerManager.class})
@Configuration
public class JmsConsumerConfig {

}
