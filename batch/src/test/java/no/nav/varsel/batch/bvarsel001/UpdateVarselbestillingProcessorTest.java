package no.nav.varsel.batch.bvarsel001;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.REVARSLING_INTERVALL;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.object.Varselbestilling;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Unit test for {@link UpdateVarselbestillingProcessor}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class UpdateVarselbestillingProcessorTest {

	private UpdateVarselbestillingProcessor processor = new UpdateVarselbestillingProcessor();

	@Test
	public void shouldUpdateValuesForRevarsel() throws Exception {
		Varselbestilling varselbestilling = processor.process(createVarselbestilling());

		assertThat(varselbestilling.getAntallRevarslinger(), is(ANTALL_REVARSLINGER - 1));
		assertThat(varselbestilling.getNesteVarslingDato(), is(LocalDate.now().plusDays(REVARSLING_INTERVALL)));
	}

	@Test
	public void shouldUpdateValuesForRevarselToNullIfLast() throws Exception {
		Varselbestilling varselbestilling = processor.process(createVarselbestillingBuilder().antallRevarslinger(1).build());

		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
		assertThat(varselbestilling.getNesteVarslingDato(), nullValue());
	}
}