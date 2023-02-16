package no.nav.varsel.repo.config;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselRepo;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackageClasses = {Varselbestilling.class})
@EnableJpaRepositories(basePackageClasses = {VarselRepo.class})
@EnableTransactionManagement
public class RepoConfig {
}
