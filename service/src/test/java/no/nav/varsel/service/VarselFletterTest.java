package no.nav.varsel.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Maps;
import no.nav.varsel.service.support.exception.FletteparameterMissingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

	private static final String DEFAULT_URL = "http://nav.no";
	private static final String URL_FROM_FASIT = "http://fasit.adeo.no";
	private static final String TIME = "2015-07-03T14:30:00";

	private VarselFletter fletter = new VarselFletter();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		fletter.setVarselUrlFromFasit(URL_FROM_FASIT);
	}

	@Test
	public void shouldFlett() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("navn", NAVN);
		map.put("aarstall", AAR);

		assertThat(fletter.weaveText("dette er en tekst om {navn} i {aarstall}", map),
				is("dette er en tekst om Even Lagsdel i 2016"));
	}

	@Test
	public void shouldFlettTimeParameter() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("tid", TIME);

		assertThat(fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.MM.yyyy} klokken {tid:HH:mm}.", map),
				is("Dette er en beskjed om at du har et møte 03.07.2015 klokken 14:30."));
	}

	@Test
	public void shouldThrowIfInvalidTimePattern() throws Exception {
		expectedException.expectMessage("Invalid format for dateTime pattern for varsel, parameter tid dd.PP.yyyy");
		Map<String, String> map = new HashMap<>();
		map.put("tid", TIME);
		fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.PP.yyyy}", map);
	}

	@Test
	public void shouldThrowIfInvalidTime() throws Exception {
		expectedException.expectMessage("Invalid format for dateTime for varsel, parameter tid 2015-07-T14:30:00");
		Map<String, String> map = new HashMap<>();
		map.put("tid", "2015-07-T14:30:00");
		fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.PP.yyyy}", map);
	}

	@Test
	public void shouldThrowIfMissingParameter() throws Exception {
		expectedException.expect(FletteparameterMissingException.class);
		expectedException.expectMessage("missing: navn tema tid");

		Map<String, String> map = new HashMap<>();
		map.put("aarstall", AAR);

		fletter.weaveText("dette er en tekst om {navn} i {aarstall} angående {tema} {tid:dd.MM.yyyy}", map);
	}

	@Test
	public void shouldDoNothingWhenNoFletteparameters() throws Exception {
		String varselUrl = fletter.weaveText(DEFAULT_URL, Maps.newHashMap());

		assertThat(varselUrl, equalTo(DEFAULT_URL));
	}

	@Test
	public void shouldFletteUrl() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");
		map.put("param2", "p2");

		String varselUrl = fletter.weaveText("http://nav.no/{param1}/{param2}", map);

		assertThat(varselUrl, equalTo("http://nav.no/p1/p2"));
	}

	@Test
	public void shouldUseFasitPropertyWhenVarselUrlContains$navnobaseurl$() throws Exception {
		String postfix = "/din-innboks";
		String prefix = "prefix";

		String varselUrl = fletter.weaveText(prefix + "$navnobaseurl$" + postfix, Maps.newHashMap());

		assertThat(varselUrl, equalTo(prefix + URL_FROM_FASIT + postfix));
	}

	@Test
	public void shouldThrowIfMissingParameterInVarselUrl() throws Exception {
		expectedException.expect(FletteparameterMissingException.class);
		expectedException.expectMessage("missing: param2");

		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");

		fletter.weaveText("http://nav.no/{param1}/{param2}", map);
	}
}