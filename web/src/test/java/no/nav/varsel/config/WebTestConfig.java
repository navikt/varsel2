package no.nav.varsel.config;

import no.nav.varsel.Application;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test config for App
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({JmsTestConfig.class, Application.class})
@Configuration
public class WebTestConfig {

}