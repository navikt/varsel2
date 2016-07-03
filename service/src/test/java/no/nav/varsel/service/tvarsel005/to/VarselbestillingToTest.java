package no.nav.varsel.service.tvarsel005.to;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for VarselbestillingTo
 * author Lars Aune
 */
public class VarselbestillingToTest {

	public static final String VARSELTYPE_ID = "VARSELTYPE_ID";
	public static final String FNR = "FNR";
	public static final String AKTOER_ID = "AKTOER_ID";
	private static final LocalDateTime BESTILLINGSTIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 1, 0, 0, 0, 0);
	private static final Integer REVARSLING_INTERVALL = 5;
	private static final LocalDateTime SISTE_VARSEL_UTSENDELSE = LocalDateTime.of(2016, Month.JULY, 3, 0, 0, 0, 0);

	@Test
	public void shouldBuild() {
		List<VarselTo> varsler = buildVarsler();

		VarselbestillingTo varselbestillingTo = buildVarselBestillingTo(varsler);

		assertVarselBestillingTo(varselbestillingTo);
	}

	public static void assertVarselBestillingTo(VarselbestillingTo varselbestillingTo) {
		assertThat(varselbestillingTo.getFnr(), is(FNR));
		assertThat(varselbestillingTo.getAktoerId(), is(AKTOER_ID));
		assertThat(varselbestillingTo.getBestillingstidspunkt(), is(BESTILLINGSTIDSPUNKT));
		assertThat(varselbestillingTo.getRevarslingsIntervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestillingTo.getSisteVarselUtsendelse(), is(SISTE_VARSEL_UTSENDELSE));
		assertThat(varselbestillingTo.getVarsler(), hasSize(1));
		VarselToTest.assertVarselTo(varselbestillingTo.getVarsler().get(0));
	}

	public static VarselbestillingTo buildVarselBestillingTo(List<VarselTo> varsler) {
		VarselbestillingTo.Builder builder = new VarselbestillingTo.Builder();
		return builder.
				varseltypeId(VARSELTYPE_ID).
				fnr(FNR).
				aktoerId(AKTOER_ID).
				bestillingstidspunkt(BESTILLINGSTIDSPUNKT).
				revarslingIntervall(REVARSLING_INTERVALL).
				sisteVarselUtsendelse(SISTE_VARSEL_UTSENDELSE).
				varsler(varsler).
				build();
	}

	public static List<VarselTo> buildVarsler() {
		List<VarselTo> varsler = new ArrayList<>();
		VarselTo varselTo = VarselToTest.buildVarselTo();
		varsler.add(varselTo);
		return varsler;
	}
}