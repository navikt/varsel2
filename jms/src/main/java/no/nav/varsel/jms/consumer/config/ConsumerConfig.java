package no.nav.varsel.jms.consumer.config;

import no.nav.varsel.jms.consumer.tvarsel001.BestillServicemeldingConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.consumer.tvarsel002.VarselKvitteringConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for JMS Consumers
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({BestillServicemeldingConsumer.class, VarselKvitteringConsumer.class})
@Configuration
public class ConsumerConfig {

	@Bean
	public BestillServicemeldingMapper bestillServicemeldingMapper() {
		return new BestillServicemeldingMapper();
	}

}
