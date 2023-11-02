package no.nav.varsel.ws.config;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CxfConfig {

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
		return new SpringBus();
	}
}
