package no.nav.varsel.service.to;

import static no.nav.varsel.service.to.BestillVarselToTest.createTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test for Tvarsel001 validator
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselToTvarsel001Test {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldValidate() throws Exception {
		createTo().validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingMottaker() throws Exception {
		expectedException.expectMessage("mottaker cannot be empty or missing");
		BestillVarselTo to = createTo();
		to.setPersonIdent(null);
		to.setAktoerId(null);
		to.validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingVarseltypeId() throws Exception {
		expectedException.expectMessage("varseltypeId cannot be empty or missing");
		BestillVarselTo to = createTo();
		to.setVarseltypeId(null);
		to.validateTvarsel001Input();
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

}