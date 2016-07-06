package no.nav.varsel.batch.bvarsel001.itest;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.REVARSLING_INTERVALL;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.object.Varselbestilling;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Itest for UpdateVarselbestillingStep Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class UpdateVarselbestillingStepTest extends AbstractBvarsel001StepTest {

	@Before
	public void setUp() throws Exception {
		createBestillingAndArbtabell();
	}

	@Test
	public void shouldUpdateVarselbestilling() throws Exception {
		launchStep("updateVarselbestillingStep");

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingId(SENDT);
		assertThat(varselbestilling.getNesteVarslingDato(), is(LocalDate.now().plusDays(REVARSLING_INTERVALL)));
		assertThat(varselbestilling.getAntallRevarslinger(), is(ANTALL_REVARSLINGER - 1));
	}

	@Test
	public void shouldNotUpdateIfWrongStatusOrMissingFromArbTabell() throws Exception {
		launchStep("updateVarselbestillingStep");

		assertThat(varselbestillingRepo.findByVarselbestillingId(IKKE_MED_I_ARB_TABELL).getAntallRevarslinger(), is(ANTALL_REVARSLINGER));
		assertThat(varselbestillingRepo.findByVarselbestillingId(OPPRETTET).getAntallRevarslinger(), is(ANTALL_REVARSLINGER));
	}
}
