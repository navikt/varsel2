package no.nav.varsel.batch.bvarsel001.itest;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.atomikos.datasource.xa.XID;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import javax.jms.Queue;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	private static int failwrite;
	private static int okwrite;

	private static XaTestResource xaTestResource = new XaTestResource();

	private static final Logger LOG = LoggerFactory.getLogger(Bvarsel001JobFailureTest.class);


	@Configuration
	public static class Config {

		@Bean
		public ItemWriter<VarselbestillingTo> varselbestillingQueueItemWriter(
				JmsQueueItemWriter<VarselbestillingTo> jmsTestWriter) {
			ClassifierCompositeItemWriter<VarselbestillingTo> writer = new ClassifierCompositeItemWriter<>();
			writer.setClassifier(vb ->
					vb.getMottakerFnr().equals(FAIL)
							|| (vb.getMottakerFnr().equals(FAILONLYONCE) && failwrite > 0)
							|| (vb.getMottakerFnr().equals(FAILONSECOND) && okwrite++ > 0)
							? items -> {
						failwrite++;
						LOG.error("varselbestillingQueueItemFailingWriter-" + failwrite);
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
		xaTestResource.clear();
		failwrite = 0;
		okwrite = 0;
		jmsTemplate.setReceiveTimeout(500L);
	}

	@Test
	public void shouldRollbackOnFailureIfFailedTwice() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder().fnr(FAIL).build());

		JobExecution jobExecution = launchJob(defaultJobParams());
		assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));
		assertThat(bvarsel001Repo.findOne(VARSELBESTILLING_ID).getArbeidStatus(), is(ArbeidStatus.OPPRETTET));
		assertJms(0);
		assertThat(failwrite, is(2));
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
		assertThat(failwrite, is(2));
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

		assertJms(1);
		assertThat(failwrite, is(1));
	}

	private void assertJms(int i) {
		for (int i1 = 0; i1 < i; i1++) {
			assertThat(jmsTemplate.receiveAndConvert(bestillVarselQueue), notNullValue());
		}
		assertThat(jmsTemplate.receiveAndConvert(bestillVarselQueue), nullValue());
	}

	@Test
	public void shouldBeAbleToRestartJob() throws Exception {
		JobParameters jobParameters = defaultJobParams();
		varselbestillingRepo.save(createVarselbestillingBuilder().fnr(FAIL).build());

		JobExecution jobExecution = launchJob(jobParameters);
		assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));
		assertThat(failwrite, is(2));
		assertThat(xaTestResource.commited, hasSize(0));

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID);
		varselbestilling.setFnr("oknow");
		varselbestillingRepo.saveAndFlush(varselbestilling);

		jobExecution = launchJob(jobParameters);
		assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
		assertThat(bvarsel001Repo.count(), is(0L));
		assertJms(1);
		assertThat(failwrite, is(2));
	}

}

class XaTestResource implements XAResource {

	final List<Object> commited = Lists.newCopyOnWriteArrayList();
	final Map<Xid, ArrayList<Object>> halfCommited = Maps.newConcurrentMap();
	final Map<Xid, ArrayList<Object>> cache = Maps.newConcurrentMap();
	int transactionTimeout = 300;

	private Xid current = new XID("x", "x");

	public boolean add(Object o) {
		return cache.get(current).add(o);
	}

	@Override
	public void commit(Xid xid, boolean b) throws XAException {
		ArrayList<Object> list = cache.get(xid);
		if (b) {
			commited.addAll(list);
		} else {
			halfCommited.get(xid).addAll(list);
		}
		cache.clear();
	}

	@Override
	public void end(Xid xid, int i) throws XAException {
		if ((i & TMSUCCESS) > 0) {
			commited.addAll(halfCommited.get(xid));
			halfCommited.get(xid).clear();

		}
		if ((i & TMFAIL) > 0) {
			halfCommited.get(xid).clear();
		}
		cache.remove(xid);
	}

	@Override
	public void forget(Xid xid) throws XAException {
		halfCommited.get(xid).clear();
	}

	@Override
	public int getTransactionTimeout() throws XAException {
		return transactionTimeout;
	}

	@Override
	public boolean isSameRM(XAResource xaResource) throws XAException {
		return xaResource.getClass().isAssignableFrom(getClass());
	}

	@Override
	public int prepare(Xid xid) throws XAException {
		return XA_OK;
	}

	@Override
	public Xid[] recover(int i) throws XAException {
		return cache.keySet().toArray(new Xid[0]);
	}

	@Override
	public void rollback(Xid xid) throws XAException {
		cache.remove(xid);
		halfCommited.remove(xid);
	}

	@Override
	public boolean setTransactionTimeout(int i) throws XAException {
		this.transactionTimeout = i;
		return true;
	}

	@Override
	public void start(Xid xid, int i) throws XAException {
		current = xid;
		if (!cache.keySet().contains(xid)) {
			cache.put(xid, new ArrayList<>());
			halfCommited.put(xid, new ArrayList<>());
		}
	}

	public void clear() {
		cache.clear();
		halfCommited.clear();
		commited.clear();
	}

}
