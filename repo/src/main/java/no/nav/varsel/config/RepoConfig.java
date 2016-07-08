package no.nav.varsel.config;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.object.worktable.Bvarsel001WorkTable;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.batch.Bvarsel001Repo;
import no.nav.varsel.repo.support.VarselRepoImpl;
import org.hibernate.SessionFactory;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

/**
 * Spring Data JPA Configuration
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({TransactionConfig.class, MetricsConfig.class})
@EntityScan(basePackageClasses = {Varselbestilling.class, Bvarsel001WorkTable.class})
@EnableJpaRepositories(basePackageClasses = {VarselRepo.class, VarselRepoImpl.class, Bvarsel001Repo.class})
@EnableTransactionManagement
public class RepoConfig {

	@Bean
	public SessionFactory sessionFactory(EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.unwrap(SessionFactory.class);
	}

}
