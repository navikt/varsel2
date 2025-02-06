package no.nav.varsel.tvarsel001.service.config;


import no.nav.varsel.consumer.config.WsConsumerTestConfig;
import no.nav.varsel.tvarsel001.service.config.ServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({WsConsumerTestConfig.class, ServiceConfig.class})
@Configuration
public class ServiceTestConfig {

}