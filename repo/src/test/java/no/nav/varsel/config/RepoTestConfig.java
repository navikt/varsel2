package no.nav.varsel.config;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.util.HashMap;
import java.util.Map;

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
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setPassword(this.properties.getPassword());
		dataSourceProperties.setUsername(this.properties.getUsername());
		dataSourceProperties.setUrl(this.properties.getUrl());
		Binder binder = new Binder(getBinderSource(dataSourceProperties));
		binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(target));
	}

	private ConfigurationPropertySource getBinderSource(DataSourceProperties dataSourceProperties) {
		Map<Object, Object> properties = new HashMap<>();
		properties.putAll(dataSourceProperties.getXa().getProperties());
		properties.computeIfAbsent("user", (key) -> dataSourceProperties.determineUsername());
		properties.computeIfAbsent("password", (key) -> dataSourceProperties.determinePassword());
		properties.computeIfAbsent("url", (key) -> dataSourceProperties.determineUrl());
		MapConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
		ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
		aliases.addAliases("user", "username");
		return source.withAliases(aliases);
	}
}
