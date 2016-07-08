package no.nav.varsel.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.ModularBatchConfiguration;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.AutomaticJobRegistrar;
import org.springframework.batch.core.configuration.support.DefaultJobLoader;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Removed @{@link EnableBatchProcessing}, configured manually to make sure JTA autoconfig goes correct.
 * Taken from {@link ModularBatchConfiguration}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class ModularBatchConfig {

	private AutomaticJobRegistrar registrar = new AutomaticJobRegistrar();

	@Bean
	public JobRepository jobRepository(PlatformTransactionManager platformTransactionManager,
									   DataSource dataSource) throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
		factory.setTransactionManager(platformTransactionManager);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean
	public JobExplorer jobExplorer(DataSource dataSource) throws Exception {
		JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
		jobExplorerFactoryBean.setDataSource(dataSource);
		jobExplorerFactoryBean.afterPropertiesSet();
		return jobExplorerFactoryBean.getObject();
	}

	@Bean
	public AutomaticJobRegistrar jobRegistrar(ApplicationContext context) throws Exception {
		registrar.setJobLoader(new DefaultJobLoader(jobRegistry()));
		for (ApplicationContextFactory factory : context.getBeansOfType(ApplicationContextFactory.class).values()) {
			registrar.addApplicationContextFactory(factory);
		}
		return registrar;
	}

	@Bean
	public JobRegistry jobRegistry() throws Exception {
		return new MapJobRegistry();
	}

	@Bean
	public JobBuilderFactory jobBuilders(JobRepository jobRepository) throws Exception {
		return new JobBuilderFactory(jobRepository);
	}

	@Bean
	public StepBuilderFactory stepBuilders(JobRepository jobRepository,
										   PlatformTransactionManager platformTransactionManager) throws Exception {
		return new StepBuilderFactory(jobRepository, platformTransactionManager);
	}

	@Bean
	public StepScope stepScope() {
		StepScope stepScope = new StepScope();
		stepScope.setAutoProxy(false);
		return stepScope;
	}

	@Bean
	public JobScope jobScope() {
		JobScope jobScope = new JobScope();
		jobScope.setAutoProxy(false);
		return jobScope;
	}

	/**
	 * Expose a {@link BatchConfigurer} bean to fool
	 * org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration.JpaBatchConfiguration#jpaBatchConfigurer(javax.sql.DataSource, javax.persistence.EntityManagerFactory)
	 *
	 * @return a dummy
	 */
	@Bean
	public BatchConfigurer batchConfigurerAutoConfigDenier() {
		return new BatchConfigurer() {
			@Override
			public JobRepository getJobRepository() throws Exception {
				return null;
			}

			@Override
			public PlatformTransactionManager getTransactionManager() throws Exception {
				return null;
			}

			@Override
			public JobLauncher getJobLauncher() throws Exception {
				return null;
			}

			@Override
			public JobExplorer getJobExplorer() throws Exception {
				return null;
			}
		};
	}
}
