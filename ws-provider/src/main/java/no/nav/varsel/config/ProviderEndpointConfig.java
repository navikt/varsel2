package no.nav.varsel.config;

import no.nav.modig.security.ws.SAMLInInterceptor;
import no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Config for Ws Endpoints
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class ProviderEndpointConfig {

	@Bean
	public EndpointImpl brukervarselV1EndpointImpl(Bus bus, BrukervarselV1Endpoint brukervarselV1Endpoint) {
		EndpointImpl endpoint = newEndpoint(bus, brukervarselV1Endpoint);
		endpoint.publish("/Brukervarsel/v1");
		return endpoint;
	}

	private EndpointImpl newEndpoint(Bus bus, Object serviceImpl) {
		EndpointImpl endpoint = new EndpointImpl(bus, serviceImpl);
		Map<String, Object> map = new HashMap<>();
		map.put(WSHandlerConstants.SIG_SUBJECT_CERT_CONSTRAINTS, ".*");
		endpoint.getInInterceptors().add(new SAMLInInterceptor(map));
		endpoint.getInInterceptors().add(new LoggingInInterceptor());
		endpoint.getOutInterceptors().add(new LoggingOutInterceptor());
		return endpoint;
	}
}
