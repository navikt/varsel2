package no.nav.varsel.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for VarselFletter
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselFletterTest {

	private static final String NAVN = "Even Lagsdel";
	private static final String AAR = "2016";

	private VarselFletter fletter = new VarselFletter();

	@Test
	public void shouldFlett() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("navn", NAVN);
		map.put("aarstall", AAR);

		assertThat(fletter.flettVarsel("dette er en tekst om :navn i :aarstall", map),
				is("dette er en tekst om Even Lagsdel i 2016"));
	}
}