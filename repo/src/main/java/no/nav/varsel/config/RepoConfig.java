package no.nav.varsel.config;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselRepo;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Data JPA Configuration
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import(TransactionConfig.class)
@EntityScan(basePackageClasses = {Varselbestilling.class})
@EnableJpaRepositories(basePackageClasses = {VarselRepo.class})
@EnableTransactionManagement
public class RepoConfig {

}
