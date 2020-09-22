package no.nav.varsel.batch.bvarsel001.itest;

import no.nav.varsel.config.BatchTestConfig;
import no.nav.varsel.domain.object.worktable.ArbeidStatus;
import no.nav.varsel.domain.object.worktable.Bvarsel001WorkTable;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Itest for CleanArbeidsTabellStep Bvarsel001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@SpringBootTest(classes = {BatchTestConfig.class})
public class CleanArbeidsTabellStepTest extends AbstractBvarsel001StepTest {

	@Test
	public void shouldCleanOnlySendtRows() throws Exception {
		bvarsel001Repo.save(new Bvarsel001WorkTable("SENDT1", ArbeidStatus.SENDT));
		bvarsel001Repo.save(new Bvarsel001WorkTable("OPPRETTET1", ArbeidStatus.OPPRETTET));
		bvarsel001Repo.save(new Bvarsel001WorkTable("SENDT2", ArbeidStatus.SENDT));
		bvarsel001Repo.save(new Bvarsel001WorkTable("OPPRETTET2", ArbeidStatus.OPPRETTET));
		bvarsel001Repo.save(new Bvarsel001WorkTable("SENDT3", ArbeidStatus.SENDT));
		bvarsel001Repo.flush();

		launchStep("cleanArbeidsTabellStep");

		assertThat(bvarsel001Repo.count(), is(2L));
		bvarsel001Repo.findAll().forEach(a -> assertTrue(a.getVarselbestillingId().startsWith("OPPRETTET")
				&& a.getArbeidStatus() == ArbeidStatus.OPPRETTET));
	}

}
