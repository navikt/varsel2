package no.nav.varsel.config;

import org.apache.catalina.realm.JAASRealm;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

@Profile("nais")
@Configuration
public class TomcatConfig {

	private static final String NAV_SAML = "NavSAML";
	private static final String JAAS_LOGIN_CONFIG = "/login.config";
	private static final String USER_CLASS_NAME = "no.nav.modig.core.domain.SluttBruker";
	private static final String ROLE_CLASS_NAME = "no.nav.modig.core.domain.ConsumerId";

	@Bean
	public ServletWebServerFactory servletContainer() {
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
		TomcatServletWebServerFactory servletContainerFactory = new TomcatServletWebServerFactory();
		servletContainerFactory.addContextCustomizers(context -> {
			JAASRealm realm = new JAASRealm();
			realm.setUserClassNames(USER_CLASS_NAME);
			realm.setRoleClassNames(ROLE_CLASS_NAME);
			realm.setAppName(NAV_SAML);
			realm.setConfigFile(new ClassPathResource(JAAS_LOGIN_CONFIG).getPath());
			context.setRealm(realm);
		});
		return servletContainerFactory;
	}
}