package no.nav.varsel.service.support;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test for {@link ValueValidator}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ValueValidatorTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void notNullOK() throws Exception {
		ValueValidator.notNull("", "field");
	}

	@Test
	public void notNull() throws Exception {
		expectedException.expectMessage("field cannot be null");
		ValueValidator.notNull(null, "field");
	}

	@Test
	public void hasTextOk() throws Exception {
		ValueValidator.hasText("tekst", "field");
	}

	@Test
	public void hasTextEmpty() throws Exception {
		expectedException.expectMessage("field cannot be empty or missing");
		ValueValidator.hasText("", "field");
	}

	@Test
	public void hasTextNull() throws Exception {
		expectedException.expectMessage("field cannot be empty or missing");
		ValueValidator.hasText(null, "field");
	}

	@Test
	public void isNumericOk() throws Exception {
		ValueValidator.isNumeric("512", "field");
	}

	@Test
	public void isNumericEmpty() throws Exception {
		expectedException.expectMessage("field cannot be empty or missing");
		ValueValidator.isNumeric("", "field");
	}

	@Test
	public void isNumericNull() throws Exception {
		expectedException.expectMessage("field cannot be empty or missing");
		ValueValidator.isNumeric(null, "field");
	}

	@Test
	public void isNumericNotNum() throws Exception {
		expectedException.expectMessage("field must be a numeric value");
		ValueValidator.isNumeric("95c", "field");
	}

}