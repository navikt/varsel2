package no.nav.varsel.config;

import no.nav.modig.security.ws.SAMLInInterceptor;
import no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint;
import no.nav.varsel.provider.ws.interceptor.ValidateSamlInInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Value;
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
	public EndpointImpl brukervarselV1EndpointImpl(Bus bus,
												   final @Value("${varsel_sigsubjectcertconstraints}") String sigSubjectCertConstraints,
												   BrukervarselV1Endpoint brukervarselV1Endpoint) {
		EndpointImpl endpoint = newEndpoint(bus, sigSubjectCertConstraints, brukervarselV1Endpoint);
		endpoint.publish("/Brukervarsel/v1");
		return endpoint;
	}

	private EndpointImpl newEndpoint(Bus bus, String sigSubjectCertConstraints, Object serviceImpl) {
		EndpointImpl endpoint = new EndpointImpl(bus, serviceImpl);

		Map<String, Object> map = new HashMap<>();
		map.put(WSHandlerConstants.SIG_SUBJECT_CERT_CONSTRAINTS, sigSubjectCertConstraints);

		endpoint.getInInterceptors().add(new ValidateSamlInInterceptor(map));
		endpoint.getInInterceptors().add(new SAMLInInterceptor(map));
		endpoint.getInInterceptors().add(new LoggingInInterceptor());
		endpoint.getOutInterceptors().add(new LoggingOutInterceptor());

		return endpoint;
	}
}
