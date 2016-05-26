package no.nav.varsel.jms.producer.config;

import no.nav.varsel.config.JmsConfig;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for JMS Producers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({JmsConfig.class})
@Configuration
public class JmsProducerConfig {

	@Bean
	public VarselutsendingProducer varselutsendingProducer() {
		return new VarselutsendingProducer();
	}
}
