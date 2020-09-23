package no.nav.varsel.config.endpoint;

import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.varsel.config.AbstractCxfEndpointConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Spring config for Kodeverk v2 CXF endpoint
 *
 * @author Lars Aune
 */
@Configuration
public class KodeverkV2Endpoint extends AbstractCxfEndpointConfig {

	private static final String WSDL_URL = "wsdl/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl";

	@Value("${kodeverkv2.ws.endpointUrl}")
	private String kodeverkUrl;

	@Bean
	public KodeverkPortType kodeverkPortType() throws IOException {
		setAdress(kodeverkUrl);
		setWsdlUrl(WSDL_URL);
		return createPort(KodeverkPortType.class);
	}
}
