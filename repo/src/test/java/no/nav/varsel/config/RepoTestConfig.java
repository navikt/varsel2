package no.nav.varsel.config;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.sql.XADataSource;

/**
 * Repository Test Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAutoConfiguration(exclude = {DataSourceTransactionManagerAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import(RepoConfig.class)
public class RepoTestConfig {

	@Inject
	private DataSourceProperties properties;

	@Bean
	@Primary
	public DataSource dataSource(XADataSourceWrapper wrapper) throws Exception {
		XADataSource dataSource = new JdbcDataSource();
		bindXaProperties(dataSource);
		return wrapper.wrapDataSource(dataSource);
	}

	@Bean
	public DataSource nonxaDataSource() {
		return DataSourceBuilder.create()
				.username(this.properties.getUsername())
				.password(this.properties.getPassword())
				.url(this.properties.getUrl())
				.build();
	}

	private void bindXaProperties(XADataSource target) {
		MutablePropertyValues values = new MutablePropertyValues();
		values.add("user", this.properties.getUsername());
		values.add("password", this.properties.getPassword());
		values.add("url", this.properties.getUrl());
		new RelaxedDataBinder(target).withAlias("user", "username").bind(values);
	}

}
