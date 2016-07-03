package no.nav.varsel.service.tvarsel005.to;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for HentVarselForBrukerResponseTo
 * @author Lars Aune
 */
public class HentVarselForBrukerResponseToTest {
	@Test
	public void shouldBuild() {
		HentVarselForBrukerResponseTo.Builder builder = new HentVarselForBrukerResponseTo.Builder();
		List<VarselTo> varsler = VarselbestillingToTest.buildVarsler();
		VarselbestillingTo varselbestillingTo = VarselbestillingToTest.buildVarselBestillingTo(varsler);
		List<VarselbestillingTo> brukersVarsler = new ArrayList<>();
		brukersVarsler.add(varselbestillingTo);

		HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo = builder.brukersVarsler(brukersVarsler).build();

		assertThat(hentVarselForBrukerResponseTo.getBrukersVarsler(), hasSize(1));
		VarselbestillingToTest.assertVarselBestillingTo(hentVarselForBrukerResponseTo.getBrukersVarsler().get(0));
	}
}