package no.nav.varsel.config;

import no.nav.brevogarkiv.batch.common.CommonBatchInputParameters;
import no.nav.brevogarkiv.batch.common.ExecutionContextWorkUnitCompletionPolicy;
import no.nav.brevogarkiv.batch.common.ExitStatusJobExecutionListener;
import no.nav.brevogarkiv.batch.common.LogContextListener;
import no.nav.brevogarkiv.batch.common.UserIdMdcJobExecutionListener;
import no.nav.brevogarkiv.batch.common.provider.launch.support.ModigJobOperator;
import no.nav.brevogarkiv.batch.common.provider.launch.support.SimpleModigJobOperator;
import no.nav.brevogarkiv.batch.common.validator.CommonJobParametersValidator;
import no.nav.varsel.domain.Constants;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

/**
 * Spring config for Spring Batch
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableBatchProcessing(modular = true)
@Import({RepoConfig.class, JmsProducerConfig.class})
public class BatchConfig {

	public static final String START_TIME_FORMAT = "dd.MM.yyyy-HH:mm:ss";

	@Bean
	public ApplicationContextFactory bvarsel001() {
		return new GenericApplicationContextFactory(Bvarsel001Config.class);
	}

	@Bean
	public ModigJobOperator modigJobOperator(JobOperator jobOperator, JobExplorer jobExplorer,
											 JobParametersConverter jobParametersConverter,
											 JobRepository jobRepository, JobRegistry jobRegistry,
											 JobLauncher jobLauncher) {
		return new SimpleModigJobOperator(jobOperator, jobExplorer, jobParametersConverter,
				jobRepository, jobRegistry, jobLauncher);
	}

	@Bean
	public JobParametersConverter jobParametersConverter() {
		return new DefaultJobParametersConverter();
	}


	@Bean
	public ExecutionContextWorkUnitCompletionPolicy workUnitCompletionPolicy() {
		return new ExecutionContextWorkUnitCompletionPolicy();
	}

	@Bean
	public LogContextListener logContextListener() {
		return new LogContextListener();
	}

	@Bean
	public UserIdMdcJobExecutionListener userIdMdcJobExecutionListener() {
		return new UserIdMdcJobExecutionListener(Constants.USER_ID);
	}

	@Bean
	public CommonJobParametersValidator.StringLongJobParameter workUnitParameter() {
		return new CommonJobParametersValidator
				.StringLongJobParameter(CommonBatchInputParameters.WORK_UNIT_KEY);
	}

	@Bean
	public CommonJobParametersValidator.StringDateJobParameter startTimeParameter() {
		return new CommonJobParametersValidator
				.StringDateJobParameter(CommonBatchInputParameters.START_TIME_KEY, START_TIME_FORMAT);
	}

	@Bean
	public CommonJobParametersValidator.StringLongJobParameter progressIntervalParameter() {
		return new CommonJobParametersValidator
				.StringLongJobParameter(CommonBatchInputParameters.PROGRESS_INTERVAL_KEY);
	}

	@Bean
	public CommonJobParametersValidator.StringLongJobParameter transactionTimeoutParameter() {
		return new CommonJobParametersValidator
				.StringLongJobParameter(CommonBatchInputParameters.TRANSACTION_TIMEOUT_KEY);
	}

	@Bean
	public CommonJobParametersValidator commonJobParametersValidator() {
		CommonJobParametersValidator jobParametersValidator = new CommonJobParametersValidator();
		jobParametersValidator.setRequiredParameters(
				Arrays.asList(
						workUnitParameter(),
						startTimeParameter()
				)
		);
		jobParametersValidator.setOptionalParameters(
				Arrays.asList(
						progressIntervalParameter(),
						transactionTimeoutParameter()
				)
		);
		return jobParametersValidator;
	}

	@Bean
	public ExitStatusJobExecutionListener exitStatusJobExecutionListener() {
		return new ExitStatusJobExecutionListener();
	}

}
