package no.nav.varsel.batch;

import com.codahale.metrics.MetricRegistry;
import no.nav.brevogarkiv.batch.common.CommonBatchInputParameters;
import no.nav.varsel.config.BatchTestConfig;
import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.domain.Constants;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract class for batch test
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"itest"})
@DirtiesContext
public abstract class AbstractBatchTest extends JobLauncherTestUtils {

	protected static final String DEFAULT_WORK_UNIT = "2";
	protected static final String DEFAULT_PROGRESS_INTERVAL = "1";

	@Inject
	protected VarselbestillingRepo varselbestillingRepo;

	@Inject
	protected JmsTemplate jmsTemplate;
	@Inject
	protected MetricRegistry metricRegistry;
	@Inject
	protected TransactionTemplate transactionTemplate;

	protected JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

	@BeforeClass
	public static void beforeClass() throws Exception {
		JmsTestConfig.mockJndi();
	}

	@Before
	public void setUpAbstract() throws Exception {
		MDC.put(Constants.USER_ID, "batch-itest");
		varselbestillingRepo.deleteAll();
	}

	@After
	public void tearDownAbstract() throws Exception {
		MDC.remove(Constants.USER_ID);
	}

	protected JobParametersBuilder getDefaultCommonJobParametersBuilder() {
		return jobParametersBuilder
				.addString(CommonBatchInputParameters.START_TIME_KEY, getStartTime())
				.addString(CommonBatchInputParameters.WORK_UNIT_KEY, getWorkUnit())
				.addString(CommonBatchInputParameters.PROGRESS_INTERVAL_KEY, getProgressInterval());
	}

	protected JobParameters defaultJobParams() {
		return getDefaultCommonJobParametersBuilder().toJobParameters();
	}

	protected String getWorkUnit() {
		return DEFAULT_WORK_UNIT;
	}

	protected String getProgressInterval() {
		return DEFAULT_PROGRESS_INTERVAL;
	}

	protected String getStartTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(BatchTestConfig.START_TIME_FORMAT_TEST));
	}

	@SuppressWarnings("unchecked")
	protected <T> T receive(Queue queue) {
		return transactionTemplate.execute(transactionStatus ->
		{
			Object response = jmsTemplate.receiveAndConvert(queue);
			if (response instanceof JAXBElement) {
				response = ((JAXBElement) response).getValue();
			}
			return (T) response;
		});
	}
}
