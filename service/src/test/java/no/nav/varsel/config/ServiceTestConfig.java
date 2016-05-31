package no.nav.varsel.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Test config for Service
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({WsConsumerTestConfig.class, ServiceConfig.class})
@Configuration
public class ServiceTestConfig {

}