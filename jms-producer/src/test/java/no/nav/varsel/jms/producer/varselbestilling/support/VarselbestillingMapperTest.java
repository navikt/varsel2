package no.nav.varsel.jms.producer.varselbestilling.support;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Person;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;
import org.junit.Test;

/**
 * Unit test for {@link VarselbestillingMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingMapperTest {

	private static final String VARSELBESTILLING_ID = "bestid";
	private static final String MOTTAKER_FNR = "fnr";
	private static final String VARSELTYPE_ID = "type";
	private static final boolean REVARSEL = true;
	private static final String PAR_1 = "par1";
	private static final String VAL_1 = "val1";
	private static final String PAR_2 = "par2";
	private static final String VAL_2 = "val2";

	private VarselbestillingMapper mapper = new VarselbestillingMapper();

	@Test
	public void shouldMap() throws Exception {
		VarselMedHandling map = mapper.map(createTo());

		assertThat(map.getVarselbestillingId(), is(VARSELBESTILLING_ID));
		assertThat(((Person) map.getMottaker()).getIdent(), is(MOTTAKER_FNR));
		assertThat(map.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(map.isReVarsel(), is(REVARSEL));
		assertThat(map.getParameterListe().get(0).getKey(), is(PAR_1));
		assertThat(map.getParameterListe().get(0).getValue(), is(VAL_1));
		assertThat(map.getParameterListe().get(1).getKey(), is(PAR_2));
		assertThat(map.getParameterListe().get(1).getValue(), is(VAL_2));
	}

	private VarselbestillingTo createTo() {
		return VarselbestillingTo.VarselbestillingToBuilder.aVarselbestillingTo()
				.varselbestillingId(VARSELBESTILLING_ID)
				.mottakerFnr(MOTTAKER_FNR)
				.varseltypeId(VARSELTYPE_ID)
				.parameter(PAR_1, VAL_1)
				.parameter(PAR_2, VAL_2)
				.revarsel(REVARSEL)
				.build();
	}
}