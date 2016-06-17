package no.nav.varsel.config;

import no.nav.varsel.config.local.LocalTomcatConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test Config for JMS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({LocalTomcatConfiguration.class, JmsTestConfig.class, RepoTestConfig.class,
		ServiceTestConfig.class, JmsConsumerConfig.class})
@Configuration
public class JmsConsumerTestConfig {

}
