package no.nav.varsel.service;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.text.StrMatcher.stringMatcher;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.varsel.service.support.exception.FletteparameterMissingException;
import no.nav.varsel.service.support.exception.FletteparameterNotUsedException;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for replacing values in a string
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselFletter {

	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{.+?\\}");
	private static final String VARSEL_URL_PARAMETER = "varselUrl";

	/**
	 * Replaces values (identified by surrounding curly brackets) in a string.
	 *
	 * @param tekst The string to replace
	 * @param flettedataInput The data to replace the values with
	 * @param varselUrl A string that will replace any occurrence of {varselUrl}
	 * @return A new string with the replaced values
	 * @throws FletteparameterMissingException Thrown if there exists a value in the text that lacks a corresponding parameter
	 * in flettedataInput
	 * @throws FletteparameterNotUsedException Thrown if not all flettedataInput parameters is used
	 */
	public String weaveVarsel(String tekst, Map<String, String> flettedataInput, String varselUrl) {
		Map<String, String> flettedata = populateFlettedata(flettedataInput, varselUrl);

		StrBuilder sb = new StrBuilder(tekst);
		Set<String> parametere = Sets.newHashSet(flettedata.keySet());
		String string = weave(flettedata, sb, parametere);

		assertNoMissedParameters(string);
		removeVarselUrlParameter(parametere);
		assertNoUnusedParameters(parametere);
		return string;
	}

	private Map<String, String> populateFlettedata(Map<String, String> flettedataInput, String varselUrl) {
		Map<String, String> flettedata = new HashMap<>(flettedataInput);
		if (varselUrl != null) {
			flettedata.put(VARSEL_URL_PARAMETER, varselUrl);
		}
		return flettedata;
	}

	private String weave(Map<String, String> flettedata, StrBuilder sb, Set<String> parametere) {
		flettedata.forEach((key, val) -> {
			String replace = "{" + key + "}";
			if (sb.contains(replace)) {
				parametere.remove(key);
			}
			sb.replace(stringMatcher(replace), val, 0, sb.length(), -1);
		});
		return sb.toString();
	}

	private void assertNoMissedParameters(String string) {
		Matcher matcher = PARAMETER_PATTERN.matcher(string);
		if (matcher.find()) {
			throw new FletteparameterMissingException(list(matcher));
		}
	}

	private void removeVarselUrlParameter(Set<String> parametere) {
		parametere.remove(VARSEL_URL_PARAMETER);
	}

	private void assertNoUnusedParameters(Set<String> parametere) {
		if (!parametere.isEmpty()) {
			throw new FletteparameterNotUsedException(Joiner.on(" ").join(parametere));
		}
	}

	private String list(Matcher matcher) {
		List<String> groups = Lists.newArrayList(matcher.group());

		while (matcher.find()) {
			groups.add(matcher.group());
		}
		groups = groups.stream().map(s -> s.substring(1, s.length() - 1)).collect(toList());
		return Joiner.on(" ").join(groups);
	}

}
