package no.nav.varsel.config;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.object.worktable.Bvarsel001WorkTable;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.support.VarselRepoImpl;
import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Spring Data JPA Configuration
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({DataSourceConfig.class, TransactionConfig.class, MetricsConfig.class})
@EntityScan(basePackageClasses = {Varselbestilling.class, Bvarsel001WorkTable.class})
@EnableJpaRepositories(basePackageClasses = {VarselRepo.class, VarselRepoImpl.class})
@EnableTransactionManagement
public class RepoConfig {

	@Bean
	public SessionFactory sessionFactory(@Named("entityManagerFactory") EntityManagerFactory emf) {
		return emf.unwrap(SessionFactory.class);
	}

	/**
	 * Sessionfactory for nonxa entity manager, used by hibernate cursors in batch, cursor used on same datasource that is
	 * committed in spring batch doesnt work with jboss/oracle/xa
	 */
	@Bean
	public SessionFactory nonxaSessionFactory(@Named("nonxaEntityManagerFactory") EntityManagerFactory emf) {
		return emf.unwrap(SessionFactory.class);
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

}
