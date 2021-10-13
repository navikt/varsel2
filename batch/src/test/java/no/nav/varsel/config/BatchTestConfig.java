package no.nav.varsel.config;

import no.nav.brevogarkiv.batch.common.validator.CommonJobParametersValidator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.START_TIME_KEY;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceTransactionManagerAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import({JmsTestConfig.class, RepoTestConfig.class, BatchConfig.class, Bvarsel001Config.class})
public class BatchTestConfig {

	public static final String START_TIME_FORMAT_TEST = "dd.MM.yyyy-HH:mm:ss.SSS";

	/**
	 * Override startTimeParameter to allow milliseconds, needed in tests.
	 */
	@Bean
	public CommonJobParametersValidator.StringDateJobParameter startTimeParameter() {
		return new CommonJobParametersValidator
				.StringDateJobParameter(START_TIME_KEY, START_TIME_FORMAT_TEST);
	}

}