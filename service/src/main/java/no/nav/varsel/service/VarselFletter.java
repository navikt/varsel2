package no.nav.varsel.service;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.text.StrMatcher.stringMatcher;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import no.nav.varsel.service.support.exception.FletteparameterMissingException;
import no.nav.varsel.service.support.exception.InvalidDateTimeFormatException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for replacing values in a varsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselFletter {

	private static final Pattern TEKST_PARAMETER_PATTERN = compile("\\{([æøåA-Z0-9\\-_]+)}", CASE_INSENSITIVE);
	private static final Pattern TIDSPUNKT_PARAMETER_PATTERN = compile("\\{([æøåA-Z0-9\\-_]+):([^}]*)}", CASE_INSENSITIVE);
	private static final String BASE_URL_IDENTIFIER = "$navnobaseurl$";

	private String varselUrlFromFasit;

	/**
	 * Replaces values (identified by surrounding curly brackets) in a string.
	 *
	 * @param tekst           The string to replace
	 * @param flettedataInput The parameters to replace the values with
	 * @return A new string with the replaced values
	 * @throws FletteparameterMissingException Thrown if there exists a value in the text that lacks a corresponding parameter
	 *                                         in flettedataInput
	 * @throws InvalidDateTimeFormatException  Thrown if a given dateTime is invalid or if the format pattern is invalid
	 */
	public String weaveText(String tekst, Map<String, String> flettedataInput)
			throws FletteparameterMissingException, InvalidDateTimeFormatException {
		String fletteTekst = tekst.replace(BASE_URL_IDENTIFIER, varselUrlFromFasit);
		String string = weave(fletteTekst, flettedataInput);

		assertNoMissedParameters(string);
		return string;
	}

	private String weave(String tekst, Map<String, String> flettedata) {
		tekst = weaveTimeParams(tekst, flettedata);
		StrBuilder sb = new StrBuilder(tekst);
		flettedata.forEach((key, val) -> {
			String replace = "{" + key + "}";
			sb.replace(stringMatcher(replace), val, 0, sb.length(), -1);
		});
		return sb.toString();
	}

	private String weaveTimeParams(String tekst, Map<String, String> flettedata) {
		Matcher timeMatcher = TIDSPUNKT_PARAMETER_PATTERN.matcher(tekst);
		while (timeMatcher.find()) {
			String key = timeMatcher.group(1);
			String pattern = timeMatcher.group(2);
			// if we cannot find it we list it later in an exception, skip for now
			if (flettedata.keySet().contains(key)) {
				String dateTime = flettedata.get(key);
				String replacement;
				try {
					LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
					replacement = parsedDateTime.format(formatter);
				} catch (DateTimeParseException e) {
					throw InvalidDateTimeFormatException.invalidDateTime(key, dateTime, e);
				} catch (IllegalArgumentException e) {
					throw InvalidDateTimeFormatException.invalidPattern(key, pattern, e);
				}
				tekst = tekst.replace(timeMatcher.group(), replacement);
			}
		}
		return tekst;
	}

	private void assertNoMissedParameters(String string) {
		Matcher matcher = TEKST_PARAMETER_PATTERN.matcher(string);
		String missingParams = "";
		if (matcher.find()) {
			missingParams = listMatches(matcher);
		}
		Matcher timeMatcher = TIDSPUNKT_PARAMETER_PATTERN.matcher(string);
		if (timeMatcher.find()) {
			missingParams += " " + listMatches(timeMatcher);
		}
		missingParams = StringUtils.strip(missingParams);
		if (StringUtils.isNotBlank(missingParams)) {
			throw new FletteparameterMissingException(missingParams);
		}
	}

	private String listMatches(Matcher matcher) {
		List<String> groups = Lists.newArrayList(matcher.group(1));

		while (matcher.find()) {
			groups.add(matcher.group(1));
		}
		return Joiner.on(" ").join(groups);
	}

	@Inject
	public void setVarselUrlFromFasit(@Value("${varsel.url}") String varselUrlFromFasit) {
		this.varselUrlFromFasit = varselUrlFromFasit;
	}

}
