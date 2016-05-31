package no.nav.varsel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
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
