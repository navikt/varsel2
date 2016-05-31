package no.nav.varsel.service;

import static org.apache.commons.lang3.text.StrMatcher.stringMatcher;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.Map;

/**
 * Helper for replacing values in a string
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselFletter {

	public String flettVarsel(String tekst, Map<String, String> parameter) {
		StrBuilder sb = new StrBuilder(tekst);
		parameter.forEach((key, val) -> sb.replace(stringMatcher(":" + key), val, 0, sb.length(), -1));
		return sb.toString();
	}

}
