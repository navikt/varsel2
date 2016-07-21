package no.nav.varsel.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test Config for JMS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableAutoConfiguration(exclude = DataSourceTransactionManagerAutoConfiguration.class)
@Import({JmsTestConfig.class, ProviderWsConfig.class})
@Configuration
public class WsProviderTestConfig {

}
