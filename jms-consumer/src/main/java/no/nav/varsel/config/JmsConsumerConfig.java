package no.nav.varsel.config;

import no.nav.varsel.jms.consumer.JmsConsumerManager;
import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.consumer.tvarsel006.BestillServicemeldingMedKontaktInfoConsumer;
import no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for JMS Consumers
 */
@Import({ServiceConfig.class, JmsConfig.class, ShutdownHook.class,
		BestillServicemeldingConsumer.class,
		BestillServicemeldingMedKontaktInfoConsumer.class})
@Configuration
public class JmsConsumerConfig {

	@Bean
	public JmsConsumerManager jmsConsumerManager() {
		return new JmsConsumerManager();
	}

	@Bean
	public BestillServicemeldingMapper bestillServicemeldingMapper() {
		return new BestillServicemeldingMapper();
	}

	@Bean
	public BestillServicemeldingMedKontaktInfoMapper serviceMeldingMedKontaktInfoMapper() {
		return new BestillServicemeldingMedKontaktInfoMapper();
	}
}
