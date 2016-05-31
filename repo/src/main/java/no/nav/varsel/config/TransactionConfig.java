package no.nav.varsel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Spring config for TransactionManager.
 * For test and local, the transaction manager is created by autoconfig by spring boot as atomikos is on the classpath.
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Profile("remote")
@Configuration
public class TransactionConfig implements TransactionManagementConfigurer {

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JtaTransactionManager();
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}
}
