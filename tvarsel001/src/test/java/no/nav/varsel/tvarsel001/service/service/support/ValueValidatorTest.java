package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.varsel.tvarsel001.service.service.support.ValueValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValueValidatorTest {

	@Test
	public void notNullOK() throws Exception {
		ValueValidator.notNull("", "field");
	}

	@Test
	public void notNull() throws Exception {
		Executable executable = () -> ValueValidator.notNull(null, "field");
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "field cannot be null");
	}

	@Test
	public void hasTextOk() throws Exception {
		ValueValidator.hasText("tekst", "field");
	}

	@Test
	public void hasTextEmpty() throws Exception {
		Executable executable = () -> ValueValidator.hasText("", "field");
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "field cannot be empty or missing");
	}

	@Test
	public void hasTextNull() throws Exception {
		Executable executable = () -> ValueValidator.hasText(null, "field");
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "field cannot be empty or missing");
	}

	@Test
	public void isNumericOk() throws Exception {
		ValueValidator.isNumeric("512", "field");
	}

	@Test
	public void isNumericEmpty() throws Exception {
		Executable executable = () -> ValueValidator.isNumeric("", "field");
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "field cannot be empty or missing");
	}

	@Test
	public void isNumericNull() throws Exception {
		Executable executable = () -> ValueValidator.isNumeric(null, "field");
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "field cannot be empty or missing");

	}

	@Test
	public void isNumericNotNum() throws Exception {
		Executable executable = () -> ValueValidator.isNumeric("95c", "field");
		Exception exception = Assertions.assertThrows(Exception.class, executable);
		assertEquals(exception.getMessage(), "field must be a numeric value");
	}

}