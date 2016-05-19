package no.nav.varsel.repo.config.local;

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
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass",
				TomcatSubjectHandler.class.getName());
		TestCertificates.setupKeyAndTrustStore();
	}
}
