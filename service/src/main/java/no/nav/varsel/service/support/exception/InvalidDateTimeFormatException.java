package no.nav.varsel.service.support.exception;

/**
 * Exception for fletting where date time pattern is invalid
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class InvalidDateTimeFormatException extends FunctionalVarselException {

	public static InvalidDateTimeFormatException invalidPattern(String key, String pattern, Throwable cause) {
		return new InvalidDateTimeFormatException(String.format("Invalid format for dateTime pattern for varsel, parameter %s %s", key, pattern), cause);
	}

	public static InvalidDateTimeFormatException invalidDateTime(String key, String dateTime, Throwable cause) {
		return new InvalidDateTimeFormatException(String.format("Invalid format for dateTime for varsel, parameter %s %s", key, dateTime), cause);
	}

	public InvalidDateTimeFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
