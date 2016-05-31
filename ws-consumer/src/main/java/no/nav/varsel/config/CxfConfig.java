package no.nav.varsel.config;

import no.nav.varsel.config.endpoint.AktoerV2Endpoint;
import no.nav.varsel.config.endpoint.DkifEndpoint;
import no.nav.varsel.config.endpoint.support.TimeoutFeature;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Collection;

/**
 * Spring Cxf Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({AktoerV2Endpoint.class, DkifEndpoint.class})
public class CxfConfig {

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
		SpringBus springBus = new SpringBus();
		Collection<Feature> features = springBus.getFeatures();
		features.add(new WSAddressingFeature());
		features.add(new TimeoutFeature());
		return springBus;
	}
}
