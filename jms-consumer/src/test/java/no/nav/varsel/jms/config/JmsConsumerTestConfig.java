package no.nav.varsel.jms.config;

import no.nav.varsel.jms.consumer.config.JmsConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test Config for JMS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({JmsTestConfig.class, JmsConsumerConfig.class})
@Configuration
public class JmsConsumerTestConfig {

}
