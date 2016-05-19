package no.nav.varsel.repo.config;

import no.nav.varsel.web.selftest.SelftestController;
import no.nav.varsel.web.selftest.test.DbSelftest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Selftest Spring config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import(SelftestController.class)
public class SelftestConfig {

	@Bean
	public DbSelftest dbTest() {
		return new DbSelftest();
	}
}
