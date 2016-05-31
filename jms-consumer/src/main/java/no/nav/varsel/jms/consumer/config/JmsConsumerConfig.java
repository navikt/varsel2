package no.nav.varsel.jms.consumer.config;

import no.nav.varsel.config.JmsConfig;
import no.nav.varsel.config.ServiceConfig;
import no.nav.varsel.jms.consumer.ConsumerManager;
import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.consumer.tvarsel002.VarselKvitteringConsumer;
import no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for JMS Consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({ServiceConfig.class, JmsConfig.class, BestillServicemeldingConsumer.class, VarselKvitteringConsumer.class})
@Configuration
public class JmsConsumerConfig {

	@Bean
	public ConsumerManager queueManager() {
		return new ConsumerManager();
	}

	@Bean
	public BestillServicemeldingMapper bestillServicemeldingMapper() {
		return new BestillServicemeldingMapper();
	}

	@Bean
	public MottaVarselKvitteringMapper mottaVarselKvitteringMapper() {
		return new MottaVarselKvitteringMapper();
	}

}
