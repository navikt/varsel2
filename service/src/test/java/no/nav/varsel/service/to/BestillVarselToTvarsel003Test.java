package no.nav.varsel.service.to;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for validation of Tvarsel003 BestillVarsel input
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselToTvarsel003Test {

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
		BestillVarselTo to = createTo();
		to.setVarselBestillingId(null);
		Executable executable = () -> to.validateTvarsel003Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "Validation failed for input, varselBestillingId cannot be empty or missing");
	}

	@Test
	public void shouldFailMissingRevarsel() throws Exception {
		BestillVarselTo to = createTo();
		to.setRevarsling(null);
		Executable executable = () -> to.validateTvarsel003Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "Validation failed for input, revarsling cannot be null");
	}

	@Test
	public void shouldFailMissingMottaker() throws Exception {
		BestillVarselTo to = createTo();
		to.setPersonIdent(null);
		Executable executable = () -> to.validateTvarsel003Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "Validation failed for input, mottaker cannot be empty or missing");

	}

	@Test
	public void shouldFailMissingVarseltypeId() throws Exception {
		BestillVarselTo to = createTo();
		to.setVarseltypeId(null);
		Executable executable = () -> to.validateTvarsel003Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "Validation failed for input, varseltypeId cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamKey() throws Exception {
		BestillVarselTo to = createTo();
		to.getParameters().put(null, "val2");
		Executable executable = () -> to.validateTvarsel001Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "Validation failed for input, parameter.key cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamValue() throws Exception {
		BestillVarselTo to = createTo();
		to.getParameters().put("key2", null);
		Executable executable = () -> to.validateTvarsel001Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "Validation failed for input, parameter.value cannot be empty or missing");
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