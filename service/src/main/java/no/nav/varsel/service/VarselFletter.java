package no.nav.varsel.service;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.text.StrMatcher.stringMatcher;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.varsel.service.support.exception.FletteparameterMissingException;
import no.nav.varsel.service.support.exception.FletteparameterNotUsedException;
import org.apache.commons.lang3.text.StrBuilder;

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

	public String flettVarsel(String tekst, Map<String, String> flettedata) {
		StrBuilder sb = new StrBuilder(tekst);
		Set<String> parametere = Sets.newHashSet(flettedata.keySet());
		flettedata.forEach((key, val) -> {
			String replace = "{" + key + "}";
			if (sb.contains(replace)) {
				parametere.remove(key);
			}
			sb.replace(stringMatcher(replace), val, 0, sb.length(), -1);
		});
		String string = sb.toString();

		assertNoUnusedParameters(parametere);
		assertNoMissedParameters(string);
		return string;
	}

	private void assertNoUnusedParameters(Set<String> parametere) {
		if (!parametere.isEmpty()) {
			throw new FletteparameterNotUsedException(Joiner.on(" ").join(parametere));
		}
	}

	private void assertNoMissedParameters(String string) {
		Matcher matcher = PARAMETER_PATTERN.matcher(string);
		if (matcher.find()) {
			throw new FletteparameterMissingException(list(matcher));
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
