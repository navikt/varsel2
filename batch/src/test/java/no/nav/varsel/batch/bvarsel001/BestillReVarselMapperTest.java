package no.nav.varsel.batch.bvarsel001;

import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.PARAMETERKEY;
import static no.nav.varsel.repo.TestdataUtil.PARAMETERVALUE;
import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;
import org.junit.Test;

/**
 * Unit test for {@link BestillReVarselMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillReVarselMapperTest {

	private BestillReVarselMapper mapper = new BestillReVarselMapper();

	@Test
	public void process() throws Exception {
		VarselbestillingTo to = mapper.process(createVarselbestilling());

		assertThat(to.getVarselbestillingId(), is(VARSELBESTILLING_ID));
		assertThat(to.getMottakerFnr(), is(FNR));
		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(to.getParameters().entrySet(), hasSize(1));
		assertThat(to.getParameters().get(PARAMETERKEY), is(PARAMETERVALUE));
		assertThat(to.isRevarsel(), is(true));
	}

}