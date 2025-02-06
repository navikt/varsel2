package no.nav.varsel.tvarsel001.jms.config;

import no.nav.varsel.tvarsel001.service.config.ServiceConfig;
import no.nav.varsel.tvarsel001.jms.consumer.JmsConsumerManager;
import no.nav.varsel.tvarsel001.jms.consumer.BestillServicemeldingConsumer;
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
