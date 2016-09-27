package no.nav.varsel.batch.bvarsel001.itest;

import static java.time.LocalDate.now;
import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingUnique;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Person;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.repo.TestdataUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

/**
 * Jobtest for Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class Bvarsel001JobTest extends AbstractBvarsel001Test {

	private static final String IGNORED = "ignored";

	@Before
	public void setUp() throws Exception {
		jmsTemplate.setReceiveTimeout(500L);
	}

	@Test
	public void shouldStartContext() throws Exception {
	}

	@Test
	public void shouldRunJob() throws Exception {
		createMultipleVarselbestillinger();
		JobExecution jobExecution = launchJob(defaultJobParams());

		assertThat(jobExecution.getExitStatus(), is(ExitStatus.COMPLETED));
		assertThat(metricRegistry.counter("BVARSEL001.enqueueVarselbestillingStep.write").getCount(), is(5L));

		assertWorktableEmpty();
		assertDb();
		assertMq();
	}

	@Test
	public void shouldSendVarselWithNesteRevarslingsDateToday() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varseltypeId(IGNORED)
				.nesteVarslingDato(now()).build());

		JobExecution jobExecution = launchJob(defaultJobParams());
		assertThat(jobExecution.getExitStatus(), is(ExitStatus.COMPLETED));

		VarselMedHandling varsel = receive(bestillVarselQueue);
		assertThat(varsel, notNullValue());
		assertThat(varsel.getVarselbestillingId(), notNullValue());
		assertThat(varsel.getVarseltypeId(), is(IGNORED));
	}

	private void createMultipleVarselbestillinger() {
		// create some that will not be picked
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varseltypeId(IGNORED).nesteVarslingDato(now().plusDays(1)).build());
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varseltypeId(IGNORED).nesteVarslingDato(null).build());

		// create varsling which will have several parameters
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.parameter("key1", "val1")
				.parameter("key2", "val2")
				.varselbestillingId(VARSELBESTILLING_ID)
				.antallRevarslinger(1).build());
		// create four more to test chunking
		for (int i = 0; i < 4; i++) {
			varselbestillingRepo.save(createVarselbestillingUnique());
		}
	}

	private void assertWorktableEmpty() {
		assertThat(bvarsel001Repo.count(), is(0L));
	}

	private void assertDb() {
		assertThat(varselbestillingRepo.findAll().stream()
				.filter(v -> !v.getVarseltypeId().equals(IGNORED)).count(), is(5L));
	}

	private void assertMq() {
		VarselMedHandling varsel = receive(bestillVarselQueue);
		assertThat(varsel, notNullValue());
		assertThat(varsel.getVarselbestillingId(), notNullValue());
		assertThat(varsel.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(((Person) varsel.getMottaker()).getIdent(), is(TestdataUtil.FNR));
		assertThat(varsel.getParameterListe(), hasSize(3));
		assertThat(varsel.getParameterListe().get(0).getKey(), is(TestdataUtil.PARAMETERKEY));
		assertThat(varsel.getParameterListe().get(0).getValue(), is(TestdataUtil.PARAMETERVALUE));

		// get 5
		assertThat(receive(bestillVarselQueue), notNullValue());
		assertThat(receive(bestillVarselQueue), notNullValue());
		assertThat(receive(bestillVarselQueue), notNullValue());
		assertThat(receive(bestillVarselQueue), notNullValue());
		assertThat(receive(bestillVarselQueue), nullValue());
	}
}
