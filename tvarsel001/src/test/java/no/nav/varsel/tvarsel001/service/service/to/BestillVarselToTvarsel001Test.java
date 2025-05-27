package no.nav.varsel.tvarsel001.service.service.to;

import org.junit.jupiter.api.Test;

import static no.nav.varsel.tvarsel001.service.service.to.BestillVarselToTest.createTo;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class BestillVarselToTvarsel001Test {

	@Test
	public void shouldValidate() {
		createTo().validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingMottaker() {
		BestillVarselTo to = createTo();
		to.setPersonIdent(null);
		to.setAktoerId(null);

		assertThatExceptionOfType(Exception.class)
				.isThrownBy(to::validateTvarsel001Input)
				.withMessage("Validation failed for input, mottaker cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingVarseltypeId() {
		BestillVarselTo to = createTo();
		to.setVarseltypeId(null);

		assertThatExceptionOfType(Exception.class)
				.isThrownBy(to::validateTvarsel001Input)
				.withMessage("Validation failed for input, varseltypeId cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamKey() {
		BestillVarselTo to = createTo();
		to.getParameters().put(null, "val2");

		assertThatExceptionOfType(Exception.class)
				.isThrownBy(to::validateTvarsel001Input)
				.withMessage("Validation failed for input, parameter.key cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamValue() {
		BestillVarselTo to = createTo();
		to.getParameters().put("key2", null);

		assertThatExceptionOfType(Exception.class)
				.isThrownBy(to::validateTvarsel001Input)
				.withMessage("Validation failed for input, parameter.value cannot be empty or missing");
	}

}