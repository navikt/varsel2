package no.nav.varsel.config;


import no.nav.varsel.consumer.config.WsConsumerTestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({WsConsumerTestConfig.class, ServiceConfig.class})
@Configuration
public class ServiceTestConfig {

}