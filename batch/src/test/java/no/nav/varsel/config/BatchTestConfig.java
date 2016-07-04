package no.nav.varsel.config;

import no.nav.brevogarkiv.batch.common.CommonBatchInputParameters;
import no.nav.brevogarkiv.batch.common.validator.CommonJobParametersValidator;
import org.springframework.batch.test.DataSourceInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Test config for Spring Batch
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@EnableAutoConfiguration
@Import({JmsTestConfig.class, BatchConfig.class, Bvarsel001Config.class})
@Configuration
public class BatchTestConfig {

	public static final String START_TIME_FORMAT_TEST = "dd.MM.yyyy-HH:mm:ss.SSS";
	@Inject
	private DataSource dataSource;

	@Value("classpath:org/springframework/batch/core/schema-h2.sql")
	private Resource hsqlcreateScript;

	@Value("classpath:org/springframework/batch/core/schema-drop-h2.sql")
	private Resource hsqldestroyScript;

	@Bean
	public DataSourceInitializer dataSourceInitializer() {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDestroyScripts(new Resource[]{hsqldestroyScript});
		initializer.destroy();
		initializer.setIgnoreFailedDrop(false);
		initializer.setInitScripts(new Resource[]{hsqlcreateScript});

		return initializer;
	}

	/**
	 * Override startTimeParameter to allow milliseconds, needed in tests.
	 */
	@Bean
	public CommonJobParametersValidator.StringDateJobParameter startTimeParameter() {
		return new CommonJobParametersValidator
				.StringDateJobParameter(CommonBatchInputParameters.START_TIME_KEY, START_TIME_FORMAT_TEST);
	}

}