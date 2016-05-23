package no.nav.varsel;

import no.nav.varsel.repo.config.RepoConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;

/**
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({RepoConfig.class})
public class ServiceConfig {

	@SuppressWarnings("unchecked")
	public static <T> T getJndiObject(String jndiName, Class<T> expectedType) {
		JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
		factory.setJndiName(jndiName);
		factory.setExpectedType(expectedType);
		try {
			factory.afterPropertiesSet();
		} catch (IllegalArgumentException | NamingException e) {
			throw new RuntimeException(e);
		}
		return (T) factory.getObject();
	}

}
