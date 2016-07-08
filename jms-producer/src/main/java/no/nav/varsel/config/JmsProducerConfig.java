package no.nav.varsel.config;

import no.nav.varsel.jms.producer.VarselbestillingProducer;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselbestilling.support.VarselbestillingMapper;
import no.nav.varsel.jms.producer.varselutsending.support.VarselutsendingMapper;
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

	@Bean
	public VarselutsendingMapper varselutsendingMapper() {
		return new VarselutsendingMapper();
	}

	@Bean
	public VarselbestillingProducer varselbestillingProducer() {
		return new VarselbestillingProducer();
	}

	@Bean
	public VarselbestillingMapper varselbestillingMapper() {
		return new VarselbestillingMapper();
	}
}
