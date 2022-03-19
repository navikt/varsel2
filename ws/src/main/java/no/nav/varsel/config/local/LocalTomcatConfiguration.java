package no.nav.varsel.config.local;

import no.nav.modig.testcertificates.TestCertificates;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * Configuration to run on local tomcat server
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Profile("local")
public class LocalTomcatConfiguration {

	public LocalTomcatConfiguration() throws IOException {
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", TomcatSubjectHandler.class.getName());
		System.setProperty("no.nav.modig.security.sts.url", "https://e34jbsl00713.devillo.no:8443/SecurityTokenServiceProvider");
		System.setProperty("varsel.serviceuser.username", "srvvarsel_u");
		System.setProperty("varsel.serviceuser.password", "CktxoGj4cc1LYQG");
		TestCertificates.setupKeyAndTrustStore();
	}
}
