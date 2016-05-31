package no.nav.varsel.config;

import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.support.VarselutsendingMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.xml.datatype.DatatypeConfigurationException;

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
	public VarselutsendingMapper varselutsendingMapper() throws DatatypeConfigurationException {
		return new VarselutsendingMapper();
	}
}
