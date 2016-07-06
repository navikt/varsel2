package no.nav.varsel.config;

import static no.nav.brevogarkiv.batch.common.CommonBatchInputParameters.TRANSACTION_TIMEOUT_KEY;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import no.nav.brevogarkiv.batch.common.BatchStatusReportLoggerListener;
import no.nav.brevogarkiv.batch.common.ExecutionContextWorkUnitCompletionPolicy;
import no.nav.brevogarkiv.batch.common.ExitStatusJobExecutionListener;
import no.nav.brevogarkiv.batch.common.LogContextListener;
import no.nav.brevogarkiv.batch.common.LoggingExceptionHandler;
import no.nav.brevogarkiv.batch.common.MetricsEnabledBatchStatusReportLoggerListener;
import no.nav.brevogarkiv.batch.common.MultiStepBatchStatusReportLoggerListener;
import no.nav.brevogarkiv.batch.common.ProgressListener;
import no.nav.brevogarkiv.batch.common.ProgressLoggerListener;
import no.nav.brevogarkiv.batch.common.TableFormatter;
import no.nav.brevogarkiv.batch.common.UserIdMdcJobExecutionListener;
import no.nav.brevogarkiv.batch.common.validator.CommonJobParametersValidator;
import no.nav.varsel.batch.bvarsel001.BestillReVarselMapper;
import no.nav.varsel.batch.bvarsel001.UpdateVarselbestillingProcessor;
import no.nav.varsel.batch.common.JmsQueueItemWriter;
import no.nav.varsel.batch.support.JdbcTasklet;
import no.nav.varsel.domain.auxiliary.AbstractDomainObject;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.varselbestilling.support.VarselbestillingMapper;
import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.CompositeJobExecutionListener;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
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
import javax.jms.Queue;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Spring config for Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class Bvarsel001Config {

	public static final String JOB_NAME = "BVARSEL001";
	public static final String LOGNAME = "no.nav.varsel.batch.bvarsel001";

	@Inject
	private JobBuilderFactory jobBuilder;
	@Inject
	private StepBuilderFactory stepBuilder;

	@Inject
	private ExecutionContextWorkUnitCompletionPolicy workUnitCompletionPolicy;
	@Inject
	private LogContextListener logContextListener;
	@Inject
	private EntityManagerFactory entityManagerFactory;

	@Bean
	public Job bvarsel001Job(
			CommonJobParametersValidator bvarsel001JobParametersValidator,
			UserIdMdcJobExecutionListener userIdMdcJobExecutionListener,
			JobExecutionListener bvarsel001BatchExitListener,
			MetricsEnabledBatchStatusReportLoggerListener metricsEnabledBatchStatusReportLoggerListener,
			Step populateArbeidsTabellStep,
			Step enqueueVarselbestillingStep,
			Step updateVarselbestillingStep,
			Step cleanArbeidsTabellStep
	) {
		return jobBuilder.get(JOB_NAME)
				.validator(bvarsel001JobParametersValidator)
				.listener(bvarsel001JobParametersValidator)
				.listener(logContextListener)
				.listener(userIdMdcJobExecutionListener)
				.listener(bvarsel001BatchExitListener)
				.listener(metricsEnabledBatchStatusReportLoggerListener)

				.start(populateArbeidsTabellStep)
				.next(enqueueVarselbestillingStep)
				.next(updateVarselbestillingStep)
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
				"WHERE neste_varsling_dato < trunc(current_date) " +
				"AND varselbestilling_id NOT IN ( SELECT varselbestilling_id FROM ARBTB_BVARSEL001)");
		return jdbcTasklet;
	}

	@Bean
	@JobScope
	public Step enqueueVarselbestillingStep(
			ItemReader<Varselbestilling> opprettetVarselbestillingWithFletteparameterReader,
			ItemWriter<VarselbestillingTo> enqueueVarselCompositeWriter,
			TransactionAttribute transactionAttribute
	) {
		return
				stepBuilder.get("enqueueVarselbestillingStep")
						.<Varselbestilling, VarselbestillingTo>chunk(workUnitCompletionPolicy)
						.faultTolerant().retryLimit(2)
						.retry(JmsException.class)

						.reader(opprettetVarselbestillingWithFletteparameterReader)
						.processor(bestillReVarselMapper())
						.writer(enqueueVarselCompositeWriter)

						.exceptionHandler(bvarsel001ExceptionHandler())
						.listener(logContextListener)
						.listener(workUnitCompletionPolicy)
						.transactionAttribute(transactionAttribute)
						.build();
	}

	@Bean
	public HibernateCursorItemReader<Varselbestilling> opprettetVarselbestillingWithFletteparameterReader(
			SessionFactory sessionFactory) {
		HibernateCursorItemReader<Varselbestilling> reader = new HibernateCursorItemReader<>();
		reader.setQueryString("select vb from Varselbestilling vb " +
				"left join fetch vb.fletteparametere " +
				"where vb.varselbestillingId in " +
				"(select arbtb.varselbestillingId from Bvarsel001WorkTable arbtb where " +
				"arbtb.arbeidStatus = 'OPPRETTET')");
		reader.setSessionFactory(sessionFactory);
		reader.setUseStatelessSession(true);
		reader.setSaveState(false);
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
			VarselbestillingMapper varselbestillingMapper
	) {
		JmsQueueItemWriter<VarselbestillingTo> jmsQueueItemWriter = new JmsQueueItemWriter<>();
		jmsQueueItemWriter.setDestination(bestillVarselQueue);
		jmsQueueItemWriter.setMapper(varselbestillingMapper);
		return jmsQueueItemWriter;
	}

	@Bean
	public Step updateVarselbestillingStep(
			ItemReader<Varselbestilling> sendtVarselbestillingReader,
			ItemProcessor<Varselbestilling, Varselbestilling> updateVarselbestillingProcessor,
			ItemWriter<? super AbstractDomainObject> jpaItemWriter,
			TransactionAttribute transactionAttribute) {
		return stepBuilder.get("updateVarselbestillingStep")
				.<Varselbestilling, Varselbestilling>chunk(workUnitCompletionPolicy)
				.reader(sendtVarselbestillingReader)
				.processor(updateVarselbestillingProcessor)
				.writer(jpaItemWriter)
				.exceptionHandler(bvarsel001ExceptionHandler())
				.listener(logContextListener)
				.listener(workUnitCompletionPolicy)
				.transactionAttribute(transactionAttribute)
				.build();
	}

	@Bean
	public HibernateCursorItemReader<Varselbestilling> sendtVarselbestillingReader(SessionFactory sessionFactory) {
		HibernateCursorItemReader<Varselbestilling> reader = new HibernateCursorItemReader<>();
		reader.setQueryString("select vb from Varselbestilling vb " +
				"where vb.varselbestillingId in " +
				"(select arbtb.varselbestillingId from Bvarsel001WorkTable arbtb where " +
				"arbtb.arbeidStatus = 'SENDT')");
		reader.setSessionFactory(sessionFactory);
		reader.setUseStatelessSession(true);
		return reader;
	}

	@Bean
	public UpdateVarselbestillingProcessor updateVarselbestillingProcessor() {
		return new UpdateVarselbestillingProcessor();
	}

	@Bean
	public JpaItemWriter<? super AbstractDomainObject> jpaItemWriter() {
		JpaItemWriter<AbstractDomainObject> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
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
	public BatchStatusReportLoggerListener bvarsel001BatchStatusReportLoggerListener(DataSource dataSource) {
		return new MultiStepBatchStatusReportLoggerListener(LOGNAME, dataSource);
	}

	@Bean
	public CompositeJobExecutionListener bvarsel001BatchExitListener(
			BatchStatusReportLoggerListener bvarsel001BatchStatusReportLoggerListener,
			ExitStatusJobExecutionListener exitStatusJobExecutionListener
	) {
		CompositeJobExecutionListener listener = new CompositeJobExecutionListener();
		// Important for ordering, last is run first
		listener.setListeners(Lists.newArrayList(bvarsel001BatchStatusReportLoggerListener, exitStatusJobExecutionListener));
		return listener;
	}

	@Bean
	public MetricsEnabledBatchStatusReportLoggerListener metricsEnabledBatchStatusReportLoggerListener(
			DataSource dataSource,
			MetricRegistry metricRegistry
	) {
		return new MetricsEnabledBatchStatusReportLoggerListener(LOGNAME, dataSource, metricRegistry);
	}
}
