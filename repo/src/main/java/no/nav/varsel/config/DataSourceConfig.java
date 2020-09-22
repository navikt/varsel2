package no.nav.varsel.config;

import no.nav.varsel.domain.object.Varselbestilling;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hibernate.cfg.AvailableSettings.JTA_PLATFORM;

/**
 * Spring config for datasources and entitymanager
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class DataSourceConfig {

	@Inject
	private JtaTransactionManager jtaTransactionManager;

	@Inject
	private JpaProperties properties;

	@Bean(destroyMethod = "")
	public DataSource dataSource(@Value("${spring.datasource.jndi-name}") String jndi) {
		return new JndiDataSourceLookup().getDataSource(jndi);
	}

	@Bean(destroyMethod = "")
	public DataSource nonxaDataSource(@Value("${nonxa.datasource.jndi-name}") String jndi) {
		return new JndiDataSourceLookup().getDataSource(jndi);
	}

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder builder, DataSource dataSource) {
		Map<String, Object> vendorProperties = getVendorProperties(dataSource);
		return builder.dataSource(dataSource).persistenceUnit("primary")
				.packages(Varselbestilling.class)
				.properties(vendorProperties).jta(true)
				.build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean nonxaEntityManagerFactory(
			EntityManagerFactoryBuilder builder, @Named("nonxaDataSource") DataSource nonxaDataSource) {
		Map<String, Object> vendorProperties = getVendorProperties(nonxaDataSource);
		return builder.dataSource(nonxaDataSource).persistenceUnit("nonxa")
				.packages(Varselbestilling.class)
				.properties(vendorProperties).jta(true)
				.build();
	}

	private Map<String, Object> getVendorProperties(DataSource dataSource) {
		Map<String, Object> vendorProperties = new LinkedHashMap<>(properties.getHibernateProperties(dataSource));
		vendorProperties.put(JTA_PLATFORM, new SpringJtaPlatform(jtaTransactionManager));
		return vendorProperties;
	}
}

