package no.nav.varsel.batch;

import com.codahale.metrics.MetricRegistry;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.config.JmsTestConfig;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.PROGRESS_INTERVAL_KEY;
import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.START_TIME_KEY;
import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.WORK_UNIT_KEY;
import static no.nav.varsel.config.BatchTestConfig.START_TIME_FORMAT_TEST;
import static no.nav.varsel.domain.Constants.USER_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"itest", "local"})
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
	public void setUpAbstract() {
		MDC.put(USER_ID, "batch-itest");
		varselbestillingRepo.deleteAll();
	}

	@After
	public void tearDownAbstract() {
		MDC.remove(USER_ID);
	}

	protected JobParametersBuilder getDefaultCommonJobParametersBuilder() {
		return jobParametersBuilder
				.addString(START_TIME_KEY, getStartTime())
				.addString(WORK_UNIT_KEY, getWorkUnit())
				.addString(PROGRESS_INTERVAL_KEY, getProgressInterval());
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
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(START_TIME_FORMAT_TEST));
	}

	protected Varselutsending findLastMessage(Queue varselutsendingQueue) {
		Varselutsending varselutsending = null;
		Varselutsending lastMessage = null;
		while((varselutsending = receive(varselutsendingQueue))!=null){
			lastMessage = varselutsending;
		}
		return lastMessage;
	}


	protected VarselMedHandling findLastMessageWithVarselMedHandling(Queue varselutsendingQueue) {
		VarselMedHandling varselutsending = null;
		VarselMedHandling lastMessage = null;
		while((varselutsending = receive(varselutsendingQueue))!=null){
			lastMessage = varselutsending;
		}
		return lastMessage;
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
