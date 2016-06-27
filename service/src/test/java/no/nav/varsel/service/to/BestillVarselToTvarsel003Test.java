package no.nav.varsel.service.to;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test for validation of Tvarsel003 BestillVarsel input
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselToTvarsel003Test {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldBeOkPersonId() throws Exception {
		createTo().validateTvarsel003Input();
	}

	@Test
	public void shouldBeOkAktoerId() throws Exception {
		BestillVarselTo to = createTo();
		to.setPersonIdent(null);
		to.setAktoerId("aktoerid");
		to.validateTvarsel003Input();
	}

	@Test
	public void shouldFailMissingVarselbestillingId() throws Exception {
		expectedException.expectMessage("varselBestillingId cannot be empty or missing");
		BestillVarselTo to = createTo();
		to.setVarselBestillingId(null);
		to.validateTvarsel003Input();
	}

	@Test
	public void shouldFailMissingRevarsel() throws Exception {
		expectedException.expectMessage("revarsling cannot be null");
		BestillVarselTo to = createTo();
		to.setRevarsling(null);
		to.validateTvarsel003Input();
	}

	@Test
	public void shouldFailMissingMottaker() throws Exception {
		expectedException.expectMessage("mottaker cannot be empty or missing");
		BestillVarselTo to = createTo();
		to.setPersonIdent(null);
		to.validateTvarsel003Input();
	}

	@Test
	public void shouldFailMissingVarseltypeId() throws Exception {
		expectedException.expectMessage("varseltypeId cannot be empty or missing");
		BestillVarselTo to = createTo();
		to.setVarseltypeId(null);
		to.validateTvarsel003Input();
	}

	@Test
	public void shouldValidateMissingParamKey() throws Exception {
		expectedException.expectMessage("parameter.key cannot be empty or missing");
		BestillVarselTo to = createTo();
		to.getParameters().put(null, "val2");
		to.validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingParamValue() throws Exception {
		expectedException.expectMessage("parameter.value cannot be empty or missing");
		BestillVarselTo to = createTo();
		to.getParameters().put("key2", null);
		to.validateTvarsel001Input();
	}

	protected BestillVarselTo createTo() {
		BestillVarselTo to = new BestillVarselTo();
		to.setVarselBestillingId("id");
		to.setRevarsling(true);
		to.setPersonIdent("pident");
		to.setVarseltypeId("varseltypeId");
		return to;
	}
}