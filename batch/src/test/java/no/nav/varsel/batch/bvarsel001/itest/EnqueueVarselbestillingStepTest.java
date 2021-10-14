package no.nav.varsel.batch.bvarsel001.itest;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Person;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.config.BatchTestConfig;
import no.nav.varsel.domain.object.worktable.ArbeidStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.PARAMETERKEY;
import static no.nav.varsel.repo.TestdataUtil.PARAMETERVALUE;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Itest for enqueueVarselbestilling Step
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Ignore
@SpringBootTest(classes = {BatchTestConfig.class})
public class EnqueueVarselbestillingStepTest extends AbstractBvarsel001StepTest {

	@Before
	public void setUp() throws Exception {
		createBestillingAndArbtabell();
	}

	@Test
	public void shouldQueueAndUpdateArbStatus() throws Exception {
		launchStep("enqueueVarselbestillingStep");

		assertThat(bvarsel001Repo.findOne(OPPRETTET).getArbeidStatus(), is(ArbeidStatus.SENDT));
		assertThat(bvarsel001Repo.findOne(SENDT).getArbeidStatus(), is(ArbeidStatus.SENDT));

		VarselMedHandling varsel = receive(bestillVarselQueue);
		assertThat(varsel, notNullValue());
		assertThat(varsel.getVarselbestillingId(), is(OPPRETTET));
		assertThat(varsel.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(((Person) varsel.getMottaker()).getIdent(), is(FNR));
		assertThat(varsel.getParameterListe(), hasSize(1));
		assertThat(varsel.getParameterListe().get(0).getKey(), is(PARAMETERKEY));
		assertThat(varsel.getParameterListe().get(0).getValue(), is(PARAMETERVALUE));

		// Only get one
		assertThat(receive(bestillVarselQueue), nullValue());
	}

}
