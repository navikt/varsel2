package no.nav.varsel.config;

import no.nav.modig.security.tilgangskontroll.config.AccessControlInterceptorConfig;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.EnvironmentRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.SecurityContextRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink.PicketLinkDecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.PEPImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

@Configuration
@Import(value = {AccessControlInterceptorConfig.class})
public class XacmlConfig {

	private String policyConfigFileName = "xacml-policy-config.xml";

	@Bean
	public EnforcementPoint pep() {
		PEPImpl pep = new PEPImpl(pdp());
		pep.setRequestEnrichers(Arrays.asList(new SecurityContextRequestEnricher(), new EnvironmentRequestEnricher()));
		return pep;
	}

	@Bean
	public DecisionPoint pdp() {
		return new PicketLinkDecisionPoint(getConfigUrl(policyConfigFileName));
	}

	private URL getConfigUrl(String path) {
		try {
			return new ClassPathResource(path).getURL();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
