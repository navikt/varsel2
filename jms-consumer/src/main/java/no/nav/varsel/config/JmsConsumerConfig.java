package no.nav.varsel.config;

import no.nav.varsel.jms.consumer.JmsConsumerManager;
import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.consumer.tvarsel002.VarselKvitteringConsumer;
import no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapper;
import no.nav.varsel.jms.consumer.tvarsel004.StoppReVarselConsumer;
import no.nav.varsel.jms.consumer.tvarsel004.support.StoppReVarselMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for JMS Consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({ServiceConfig.class, JmsConfig.class, BestillServicemeldingConsumer.class, VarselKvitteringConsumer.class,
		StoppReVarselConsumer.class})
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
	public MottaVarselKvitteringMapper mottaVarselKvitteringMapper() {
		return new MottaVarselKvitteringMapper();
	}

	@Bean
	public StoppReVarselMapper stoppReVarselMapper() {
		return new StoppReVarselMapper();
	}
}
