package no.nav.varsel.service.support;

import org.springframework.util.Assert;

/**
 * Validators
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ValueValidator {

	public static void notNull(Object field, String fieldName) {
		Assert.notNull(field, String.format("%s cannot be null", fieldName));
	}

	public static void hasText(String field, String fieldName) {
		Assert.hasText(field, String.format("%s cannot be empty or missing", fieldName));
	}

	public static void isNumeric(String field, String fieldName) {
		hasText(field, fieldName);
		Assert.isTrue(field.matches("\\d+"), String.format("%s must be a numeric value", fieldName));
	}

}
