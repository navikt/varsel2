package no.nav.varsel.batch.bvarsel001.itest;

import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.object.worktable.ArbeidStatus;
import no.nav.varsel.domain.object.worktable.Bvarsel001WorkTable;
import org.junit.After;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobScopeTestExecutionListener;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Abstract for Step tests in Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, JobScopeTestExecutionListener.class})
public abstract class AbstractBvarsel001StepTest extends AbstractBvarsel001Test {

	static final String IKKE_MED_I_ARB_TABELL = "ikkeMedIArbTabell";
	static final String SENDT = "sendt";
	static final String OPPRETTET = "opprettet";

	private JobExecution jobExecution;
	private ExecutionContext executionContext;

	@After
	public void tearDownAbstractStep() throws Exception {
		jobExecution = null;
		executionContext = null;
	}

	public void createExecution() {
		jobExecution = MetaDataInstanceFactory.createJobExecution();
		executionContext = jobExecution.getExecutionContext();
		executionContext.put("workUnit", Long.valueOf(DEFAULT_WORK_UNIT));
	}

	public JobExecution getJobExecution() {
		if (jobExecution == null) {
			createExecution();
		}
		return jobExecution;
	}

	@Override
	public JobExecution launchStep(String stepName) {
		JobExecution jobExecution = launchStep(stepName, defaultJobParams(), executionContext);
		assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
		return jobExecution;
	}

	void createBestillingAndArbtabell() {
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(OPPRETTET).build());
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(SENDT).build());
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(IKKE_MED_I_ARB_TABELL).build());

		bvarsel001Repo.save(new Bvarsel001WorkTable(SENDT, ArbeidStatus.SENDT));
		bvarsel001Repo.save(new Bvarsel001WorkTable(OPPRETTET, ArbeidStatus.OPPRETTET));
	}
}
