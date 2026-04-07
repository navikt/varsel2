package no.nav.varsel.repo.config;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/// Delt H2-datasource-konfigurasjon for integrasjonstester.
///
/// * `DB_CLOSE_DELAY`: hindrer at H2 dropper databasen hvis en kobling lukkes
/// * `NON_KEYWORDS`: tabellen `FLETTE_PARAMETER` har reserverte keywords som kolonnenavn
@Configuration
public class H2TestDataSourceConfig {

	@Bean
	@Primary
	public DataSource dataSource() {
		JdbcDataSource jdbcDataSource = new JdbcDataSource();
		jdbcDataSource.setUrl("jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1;NON_KEYWORDS=KEY,VALUE");
		return jdbcDataSource;
	}

}
