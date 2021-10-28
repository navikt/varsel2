package no.nav.varsel.config;

import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.PROGRESS_INTERVAL_KEY;
import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.TRANSACTION_TIMEOUT_KEY;
import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.WORK_UNIT_KEY;
import static no.nav.brevogarkiv.batch.common.MetricsEnabledBatchStatusReportLoggerListener.StepCountType.WRITE;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import no.nav.brevogarkiv.batch.common.ExecutionContextWorkUnitCompletionPolicy;
import no.nav.brevogarkiv.batch.common.ExitStatusJobExecutionListener;
import no.nav.brevogarkiv.batch.common.LogContextListener;
import no.nav.brevogarkiv.batch.common.LoggingExceptionHandler;
import no.nav.brevogarkiv.batch.common.MetricsEnabledBatchStatusReportLoggerListener;
import no.nav.brevogarkiv.batch.common.ProgressListener;
import no.nav.brevogarkiv.batch.common.ProgressLoggerListener;
import no.nav.brevogarkiv.batch.common.TableFormatter;
import no.nav.brevogarkiv.batch.common.UserIdMdcJobExecutionListener;
import no.nav.brevogarkiv.batch.common.validator.CommonJobParametersValidator;
import no.nav.varsel.batch.bvarsel001.BestillReVarselMapper;
import no.nav.varsel.batch.common.JmsQueueItemWriter;
import no.nav.varsel.batch.support.FaultTolerantStepBuilderLazyTransactionAttribute;
import no.nav.varsel.batch.support.JdbcTasklet;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.varselbestilling.support.BestillVarselProducerMapper;
import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.CompositeJobExecutionListener;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.JmsException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Queue;
import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Spring config for Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class Bvarsel001Config {

	private static final String JOB_NAME = "BVARSEL001";
	private static final String LOGNAME = "no.nav.varsel.batch.bvarsel001";

	@Inject
	private JobBuilderFactory jobBuilder;
	@Inject
	private StepBuilderFactory stepBuilder;

	@Inject
	private ExecutionContextWorkUnitCompletionPolicy workUnitCompletionPolicy;
	@Inject
	private LogContextListener logContextListener;

	@Bean
	public Job bvarsel001Job(
			CommonJobParametersValidator bvarsel001JobParametersValidator,
			UserIdMdcJobExecutionListener userIdMdcJobExecutionListener,
			JobExecutionListener bvarsel001ExecutionListener,
			Step populateArbeidsTabellStep,
			Step enqueueVarselbestillingStep,
			Step cleanArbeidsTabellStep
	) {
		return jobBuilder.get(JOB_NAME)
				.validator(bvarsel001JobParametersValidator)
				.listener(bvarsel001JobParametersValidator)
				.listener(logContextListener)
				.listener(userIdMdcJobExecutionListener)
				.listener(bvarsel001ExecutionListener)

				.start(populateArbeidsTabellStep)
				.next(enqueueVarselbestillingStep)
				.next(cleanArbeidsTabellStep)

				.build();
	}

	@Bean
	public Step populateArbeidsTabellStep(TransactionAttribute transactionAttribute) {
		return stepBuilder.get("populateArbeidsTabellStep")
				.tasklet(populateArbeidsTabellTasklet())
				.exceptionHandler(bvarsel001ExceptionHandler())
				.listener(logContextListener)
				.transactionAttribute(transactionAttribute)
				.build();
	}

	@Bean
	public Tasklet populateArbeidsTabellTasklet() {
		JdbcTasklet jdbcTasklet = new JdbcTasklet();
		jdbcTasklet.setSql("INSERT " +
				"INTO ARBTB_BVARSEL001 (varselbestilling_id) " +
				"SELECT varselbestilling_id FROM VARSELBESTILLING " +
				"WHERE neste_varsling_dato <= trunc(current_date) " +
				"AND antall_revarslinger > 0 " +
				"AND varselbestilling_id NOT IN ( SELECT varselbestilling_id FROM ARBTB_BVARSEL001)");
		return jdbcTasklet;
	}

	@Bean
	public Step enqueueVarselbestillingStep(
			HibernateCursorItemReaderVarselbestilling opprettetVarselbestillingWithFletteparameterReader,
			ItemWriter<VarselbestillingTo> enqueueVarselCompositeWriter,
			TransactionAttribute transactionAttribute,
			StepExecutionListener bvarsel001BatchStatusReportLoggerListener
	) {
		FaultTolerantStepBuilder<Varselbestilling, VarselbestillingTo> builder =
				new FaultTolerantStepBuilderLazyTransactionAttribute<>(stepBuilder.get("enqueueVarselbestillingStep")
						.chunk(workUnitCompletionPolicy));

		return builder
				.retryLimit(2)
				.retry(JmsException.class)

				.reader(opprettetVarselbestillingWithFletteparameterReader)
				.processor(bestillReVarselMapper())
				.writer(enqueueVarselCompositeWriter)

				.exceptionHandler(bvarsel001ExceptionHandler())
				.listener(logContextListener)
				.listener(workUnitCompletionPolicy)
				.listener(bvarsel001BatchStatusReportLoggerListener)
				.transactionAttribute(transactionAttribute)
				.build();
	}

	@Bean
	@JobScope
	public HibernateCursorItemReaderVarselbestilling opprettetVarselbestillingWithFletteparameterReader(
			@Named("nonxaSessionFactory")
			SessionFactory nonxaSessionFactory,
			@Value("#{jobParameters[" + WORK_UNIT_KEY + "]}") int workUnit) {
		HibernateCursorItemReaderVarselbestilling reader = new HibernateCursorItemReaderVarselbestilling();
		reader.setQueryString("select vb from Varselbestilling vb " +
				"left join fetch vb.fletteparametere " +
				"where vb.varselbestillingId in " +
				"(select arbtb.varselbestillingId from Bvarsel001WorkTable arbtb where " +
				"arbtb.arbeidStatus = 'OPPRETTET')");
		reader.setSessionFactory(nonxaSessionFactory);
		reader.setUseStatelessSession(true);
		reader.setSaveState(false);
		reader.setFetchSize(workUnit);
		return reader;
	}

	@Bean
	public BestillReVarselMapper bestillReVarselMapper() {
		return new BestillReVarselMapper();
	}

	@Bean
	public CompositeItemWriter<VarselbestillingTo> enqueueVarselCompositeWriter(
			ItemWriter<VarselbestillingTo> arbeidstabellStatusUpdater,
			ItemWriter<VarselbestillingTo> varselbestillingQueueItemWriter) {
		CompositeItemWriter<VarselbestillingTo> writer = new CompositeItemWriter<>();
		ArrayList<ItemWriter<? super VarselbestillingTo>> delegates = Lists.newArrayList();
		delegates.add(arbeidstabellStatusUpdater);
		delegates.add(varselbestillingQueueItemWriter);
		writer.setDelegates(delegates);
		return writer;
	}

	@Bean
	public JdbcBatchItemWriter<VarselbestillingTo> arbeidstabellStatusUpdater(DataSource dataSource) {
		JdbcBatchItemWriter<VarselbestillingTo> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource);
		writer.setSql("UPDATE ARBTB_BVARSEL001 SET ARBEID_STATUS = 'SENDT' WHERE VARSELBESTILLING_ID = :varselbestillingId");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		return writer;
	}

	@Bean
	public JmsQueueItemWriter<VarselbestillingTo> varselbestillingQueueItemWriter(
			Queue bestillVarselQueue,
			BestillVarselProducerMapper bestillVarselProducerMapper
	) {
		JmsQueueItemWriter<VarselbestillingTo> jmsQueueItemWriter = new JmsQueueItemWriter<>();
		jmsQueueItemWriter.setDestination(bestillVarselQueue);
		jmsQueueItemWriter.setMapper(bestillVarselProducerMapper);
		return jmsQueueItemWriter;
	}

	@Bean
	public Step cleanArbeidsTabellStep(TransactionAttribute transactionAttribute) {
		return stepBuilder.get("cleanArbeidsTabellStep")
				.tasklet(cleanArbeidsTabellTasklet())
				.exceptionHandler(bvarsel001ExceptionHandler())
				.listener(logContextListener)
				.transactionAttribute(transactionAttribute)
				.build();
	}

	@Bean
	public JdbcTasklet cleanArbeidsTabellTasklet() {
		JdbcTasklet jdbcTasklet = new JdbcTasklet();
		jdbcTasklet.setSql("DELETE FROM arbtb_bvarsel001 WHERE arbeid_status = 'SENDT'");
		return jdbcTasklet;
	}

	@Bean
	@JobScope
	public TransactionAttribute transactionAttribute(
			@Value("#{jobParameters[" + TRANSACTION_TIMEOUT_KEY + "] ?: defaultTimeout}") int timeout) {
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
		transactionAttribute.setTimeout(timeout);
		return transactionAttribute;
	}

	@Bean
	public Integer defaultTimeout(AbstractPlatformTransactionManager transactionManager) {
		return transactionManager.getDefaultTimeout();
	}

	@Bean
	public CommonJobParametersValidator bvarsel001JobParametersValidator(
			CommonJobParametersValidator commonJobParametersValidator) {
		CommonJobParametersValidator bdist001JobParametersValidator = new CommonJobParametersValidator();
		bdist001JobParametersValidator.setRequiredParameters(commonJobParametersValidator.getRequiredParameters());
		bdist001JobParametersValidator.setOptionalParameters(commonJobParametersValidator.getOptionalParameters());
		return bdist001JobParametersValidator;
	}

	@Bean
	public ExceptionHandler bvarsel001ExceptionHandler() {
		return new LoggingExceptionHandler(LOGNAME);
	}

	@Bean
	public ProgressListener bvarsel001ProgressListener() {
		return new ProgressLoggerListener(LOGNAME, new TableFormatter());
	}

	@Bean
	public CompositeJobExecutionListener bvarsel001ExecutionListener(
			JobExecutionListener bvarsel001BatchStatusReportLoggerListener,
			ExitStatusJobExecutionListener exitStatusJobExecutionListener
	) {
		CompositeJobExecutionListener listener = new CompositeJobExecutionListener();
		// Important for ordering, last is run first
		listener.setListeners(Lists.newArrayList(bvarsel001BatchStatusReportLoggerListener, exitStatusJobExecutionListener));
		return listener;
	}

	@JobScope
	@Bean
	public MetricsEnabledBatchStatusReportLoggerListener bvarsel001BatchStatusReportLoggerListener(
			DataSource dataSource,
			MetricRegistry metricRegistry,
			@Value("#{jobParameters[" + PROGRESS_INTERVAL_KEY + "] ?: 1}") int progressInterval
	) {
		MetricsEnabledBatchStatusReportLoggerListener listener = new MetricsEnabledBatchStatusReportLoggerListener(LOGNAME, dataSource, metricRegistry);
		listener.addProgressCounter("enqueueVarselbestillingStep", "Revarsel bestilt", WRITE);
		listener.setProgressInterval(progressInterval);
		return listener;
	}

	/**
	 * generic beans in job/step-scope doesn't work due to bug in springbatch in spring4, see BATCH-2413
	 */
	private static class HibernateCursorItemReaderVarselbestilling extends HibernateCursorItemReader<Varselbestilling> {
		public HibernateCursorItemReaderVarselbestilling(){
			super();
		}
	}
}
