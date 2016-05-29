package no.nav.varsel.repo.config;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselRepo;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Spring Data JPA Configuration
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EntityScan(basePackageClasses = {Varselbestilling.class})
@EnableJpaRepositories(basePackageClasses = {VarselRepo.class})
@EnableTransactionManagement
public class RepoConfig implements TransactionManagementConfigurer {

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JtaTransactionManager();
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}
}
