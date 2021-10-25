package no.nav.varsel.batch.bvarsel001.itest;

import no.nav.varsel.config.BatchTestConfig;
import no.nav.varsel.domain.object.worktable.ArbeidStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static java.time.LocalDate.now;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Itest for PopulateArbeidsTabellStep Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@SpringBootTest(classes = {BatchTestConfig.class})
public class PopulateArbeidsTabellStepTest extends AbstractBvarsel001StepTest {

	private static final String YESTERDAY = "yesterday";
	private static final String LAST_WEEK = "last_week";
	private static final String TODAY = "today";

	@Before
	public void setUp() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varselbestillingId(TODAY).nesteVarslingDato(now()).build());
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varselbestillingId("tomorrow_not_picked").nesteVarslingDato(now().plusDays(1)).build());
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varselbestillingId("nest_week_not_picked").nesteVarslingDato(now().plusWeeks(1)).build());
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varselbestillingId(YESTERDAY).nesteVarslingDato(now().minusDays(1)).build());
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varselbestillingId(LAST_WEEK).nesteVarslingDato(now().minusWeeks(1)).build());
	}

	@Test
	public void shouldPopulateArbeidsTabell() throws Exception {
		launchStep("populateArbeidsTabellStep");


		assertThat(bvarsel001Repo.findById(YESTERDAY).get().getArbeidStatus(), is(ArbeidStatus.OPPRETTET));
		assertThat(bvarsel001Repo.findById(LAST_WEEK).get().getArbeidStatus(), is(ArbeidStatus.OPPRETTET));
		assertThat(bvarsel001Repo.findById(TODAY).get().getArbeidStatus(), is(ArbeidStatus.OPPRETTET));
		assertThat(bvarsel001Repo.count(), is(3L));
	}
}