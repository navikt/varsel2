package no.nav.varsel.batch.bvarsel001.itest;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.REVARSLING_INTERVALL;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.object.worktable.ArbeidStatus;
import no.nav.varsel.domain.object.worktable.Bvarsel001WorkTable;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Itest for UpdateVarselbestillingStep Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class UpdateVarselbestillingStepTest extends AbstractBvarsel001StepTest {

	private static final String IKKE_MED_I_ARB_TABELL = "ikkeMedIArbTabell";
	private static final String SENDT = "sendt";
	public static final String OPPRETTET = "opprettet";

	@Before
	public void setUp() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(OPPRETTET).build());
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(SENDT).build());
		varselbestillingRepo.save(createVarselbestillingBuilder().varselbestillingId(IKKE_MED_I_ARB_TABELL).build());

		bvarsel001Repo.save(new Bvarsel001WorkTable(SENDT, ArbeidStatus.SENDT));
		bvarsel001Repo.save(new Bvarsel001WorkTable(OPPRETTET, ArbeidStatus.OPPRETTET));
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
