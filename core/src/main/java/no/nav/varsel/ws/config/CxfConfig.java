package no.nav.varsel.ws.config;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.context.annotation.Bean;

/**
 * Spring Cxf Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class CxfConfig {

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
		return new SpringBus();
	}
}
