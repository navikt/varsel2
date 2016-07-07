package no.nav.varsel.batch.bvarsel001.itest;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.varsel.batch.common.JmsQueueItemWriter;
import no.nav.varsel.config.BatchTestConfig;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.object.worktable.ArbeidStatus;
import no.nav.varsel.jms.producer.varselbestilling.support.VarselbestillingMapper;
import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.jms.Queue;
import java.util.UUID;

/**
 * Itest for Failure in jobtest
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@SpringApplicationConfiguration(classes = {Bvarsel001JobFailureTest.Config.class, BatchTestConfig.class})
public class Bvarsel001JobFailureTest extends AbstractBvarsel001Test {

	private static final String FAIL = "FAIL";
	private static final String FAILONLYONCE = "FAILONLYONCE";
	private static final String FAILONSECOND = "FAILONSECOND";
	private static int triggerFailure;
	private static int failureCount;
	private static int okwrite;

	private static final Logger LOG = LoggerFactory.getLogger(Bvarsel001JobFailureTest.class);

	@Inject
	TransactionTemplate transactionTemplate;

	@Configuration
	public static class Config {

		@Bean
		public ItemWriter<VarselbestillingTo> varselbestillingQueueItemWriter(
				JmsQueueItemWriter<VarselbestillingTo> jmsTestWriter) {
			ClassifierCompositeItemWriter<VarselbestillingTo> writer = new ClassifierCompositeItemWriter<>();
			writer.setClassifier(vb ->
					vb.getMottakerFnr().equals(FAIL)
							|| (vb.getMottakerFnr().equals(FAILONLYONCE) && triggerFailure++ == 0)
							|| (vb.getMottakerFnr().equals(FAILONSECOND) && triggerFailure++ > 0)
							? items -> {
						failureCount++;
						LOG.error("varselbestillingQueueItemFailingWriter-" + failureCount);
						throw new UncategorizedJmsException("testfail");
					} : jmsTestWriter

			);
			return writer;
		}

		/**
		 * Copy from {@link no.nav.varsel.config.Bvarsel001Config}
		 */
		@Bean
		public JmsQueueItemWriter<VarselbestillingTo> jmsTestWriter(
				Queue bestillVarselQueue,
				VarselbestillingMapper varselbestillingMapper) {
			JmsQueueItemWriter<VarselbestillingTo> jmsQueueItemWriter = new JmsQueueItemWriter<>();
			jmsQueueItemWriter.setDestination(bestillVarselQueue);
			jmsQueueItemWriter.setMapper(varselbestillingMapper);
			return jmsQueueItemWriter;
		}
	}

	@Before
	public void setUp() throws Exception {
		triggerFailure = 0;
		failureCount = 0;
		jmsTemplate.setReceiveTimeout(500L);
	}

	@Test
	public void shouldRollbackOnFailureIfFailedTwice() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder().fnr(FAIL).build());

		JobExecution jobExecution = launchJob(defaultJobParams());
		assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));
		assertThat(bvarsel001Repo.findOne(VARSELBESTILLING_ID).getArbeidStatus(), is(ArbeidStatus.OPPRETTET));
		assertJms(0);
		assertThat(failureCount, is(2));
	}

	@Test
	public void shouldRollbackJms() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder().fnr(FAILONSECOND).build());
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(UUID.randomUUID().toString())
				.fnr(FAILONSECOND).build());

		JobExecution jobExecution = launchJob(defaultJobParams());
		assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));
		assertThat(bvarsel001Repo.findOne(VARSELBESTILLING_ID).getArbeidStatus(), is(ArbeidStatus.OPPRETTET));
		assertJms(0);
		assertThat(failureCount, is(2));
	}

	@Test
	public void shouldRetryOnSingleFailureAndBeOk() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder().fnr(FAILONLYONCE).build());
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(UUID.randomUUID().toString())
				.fnr(FAILONLYONCE).build());

		JobExecution jobExecution = launchJob(defaultJobParams());
		assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
		assertThat(bvarsel001Repo.count(), is(0L));
		assertThat(varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID).getAntallRevarslinger(),
				is(ANTALL_REVARSLINGER - 1));

		assertJms(2);
		assertThat(failureCount, is(1));
	}

	private void assertJms(int i) {
		for (int i1 = 0; i1 < i; i1++) {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
					assertThat(jmsTemplate.receiveAndConvert(bestillVarselQueue), notNullValue());
				}
			});
		}
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
				assertThat(jmsTemplate.receiveAndConvert(bestillVarselQueue), nullValue());
			}
		});

	}

	@Test
	public void shouldBeAbleToRestartJob() throws Exception {
		JobParameters jobParameters = defaultJobParams();
		varselbestillingRepo.save(createVarselbestillingBuilder().fnr(FAIL).build());

		JobExecution jobExecution = launchJob(jobParameters);
		assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));
		assertThat(failureCount, is(2));
		assertJms(0);

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID);
		varselbestilling.setFnr("oknow");
		varselbestillingRepo.saveAndFlush(varselbestilling);

		jobExecution = launchJob(jobParameters);
		assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
		assertThat(bvarsel001Repo.count(), is(0L));
		assertJms(1);
		assertThat(failureCount, is(2));
	}

}
