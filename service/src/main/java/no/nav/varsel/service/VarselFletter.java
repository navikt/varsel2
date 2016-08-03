package no.nav.varsel.service;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.text.StrMatcher.stringMatcher;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.varsel.service.support.exception.FletteparameterMissingException;
import no.nav.varsel.service.support.exception.FletteparameterNotUsedException;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.Iterator;
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
	private static final String BASE_URL_IDENTIFIER = "$navnobaseurl$";

	private String varselUrlFromFasit;


	/**
	 * Weaves a url.
	 * $navnobaseurl$ is replaced by variable varselUrlFromFasit.
	 * Every value surrounded by curly bracket is replaced with values from the flettedataInput parameter
	 * The method consumes the parameters in flettedataInput, and will remove any the parameters
	 *
	 * @param urlInput The url to weave
	 * @param flettedataInput The parameter map
	 * @return The new string
	 */
	public String weaveVarselUrl(String urlInput, Map<String, String> flettedataInput) {
		if (urlInput == null) {
			return null;
		}
		String varselUrl = urlInput.replace(BASE_URL_IDENTIFIER, varselUrlFromFasit);

		Set<String> params = Sets.newHashSet(flettedataInput.keySet());
		String weavedUrl = weave(varselUrl, flettedataInput, params);

		assertNoMissedParameters(weavedUrl);
		//Hack to enable assertNoUnusedParameters to work in weaveVarsel. May cause UnsupportedOperationException
		removeUsedParameters(flettedataInput, params);
		return weavedUrl;
	}

	/**
	 * Replaces values (identified by surrounding curly brackets) in a string.
	 *
	 * @param tekst The string to replace
	 * @param flettedataInput The parameters to replace the values with
	 * @return A new string with the replaced values
	 * @throws FletteparameterMissingException Thrown if there exists a value in the text that lacks a corresponding parameter
	 * in flettedataInput
	 * @throws FletteparameterNotUsedException Thrown if not all flettedataInput parameters is used
	 */
	public String weaveVarsel(String tekst, Map<String, String> flettedataInput) {
		Set<String> parametere = Sets.newHashSet(flettedataInput.keySet());
		String string = weave(tekst, flettedataInput, parametere);

		assertNoMissedParameters(string);
		assertNoUnusedParameters(parametere);
		return string;
	}

	private void removeUsedParameters(Map<String, String> flettedataInput, Set<String> unusedParams) {
		Iterator<Map.Entry<String, String>> iterator = flettedataInput.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			if (!unusedParams.contains(entry.getKey())) {
				iterator.remove();
			}
		}
	}



	private String weave(String tekst, Map<String, String> flettedata, Set<String> parametere) {
		StrBuilder sb = new StrBuilder(tekst);
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

	@Inject
	public void setVarselUrlFromFasit(@Value("${varsel.url}") String varselUrlFromFasit) {
		this.varselUrlFromFasit = varselUrlFromFasit;
	}

}
