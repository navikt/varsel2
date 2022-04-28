package no.nav.varsel.config;

import no.nav.varsel.kafka.CustomKafkaTemplate;
import no.nav.varsel.tvarsel006.NotifikasjonMedKontaktinfoPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test Config for JMS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({JmsTestConfig.class, RepoTestConfig.class,
		ServiceTestConfig.class, JmsConsumerConfig.class,
		NotifikasjonMedKontaktinfoPublisher.class,
		CustomKafkaTemplate.class
})
@Configuration
public class JmsConsumerTestConfig {

}
