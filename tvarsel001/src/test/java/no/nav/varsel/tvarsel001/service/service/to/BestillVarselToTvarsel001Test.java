package no.nav.varsel.tvarsel001.service.service.to;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static no.nav.varsel.tvarsel001.service.service.to.BestillVarselToTest.createTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BestillVarselToTvarsel001Test {

	@Test
	public void shouldValidate() throws Exception {
		createTo().validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingMottaker() throws Exception {
		BestillVarselTo to = createTo();
		to.setPersonIdent(null);
		to.setAktoerId(null);
		Executable executable = () -> to.validateTvarsel001Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, mottaker cannot be empty or missing");

	}

	@Test
	public void shouldValidateMissingVarseltypeId() throws Exception {
		BestillVarselTo to = createTo();
		to.setVarseltypeId(null);
		Executable executable = () -> to.validateTvarsel001Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, varseltypeId cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamKey() throws Exception {
		BestillVarselTo to = createTo();
		to.getParameters().put(null, "val2");
		Executable executable = () -> to.validateTvarsel001Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, parameter.key cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamValue() throws Exception {
		BestillVarselTo to = createTo();
		to.getParameters().put("key2", null);
		Executable executable = () -> to.validateTvarsel001Input();
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, parameter.value cannot be empty or missing");
	}

}