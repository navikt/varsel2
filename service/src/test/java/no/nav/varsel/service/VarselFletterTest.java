package no.nav.varsel.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Maps;
import no.nav.varsel.service.support.exception.FletteparameterMissingException;
import no.nav.varsel.service.support.exception.FletteparameterNotUsedException;
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
	private static final String DEFAULT_TEXT = "dette er en tekst om året ";

	private static final String DEFAULT_URL = "http://nav.no";
	private static final String URL_FROM_FASIT = "http://fasit.adeo.no";

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

		assertThat(fletter.weaveVarsel("dette er en tekst om {navn} i {aarstall}", map),
				is("dette er en tekst om Even Lagsdel i 2016"));
	}

	@Test
	public void shouldThrowIfMissingParameter() throws Exception {
		expectedException.expect(FletteparameterMissingException.class);
		expectedException.expectMessage("missing: navn tema");

		Map<String, String> map = new HashMap<>();
		map.put("aarstall", AAR);

		fletter.weaveVarsel("dette er en tekst om {navn} i {aarstall} angående {tema}", map);
	}

	@Test
	public void shouldThrowIfMissingParameterValueInTekst() throws Exception {
		expectedException.expect(FletteparameterNotUsedException.class);
		expectedException.expectMessage("unused: tema");

		Map<String, String> map = new HashMap<>();
		map.put("aarstall", AAR);
		map.put("tema", "klage");

		fletter.weaveVarsel(DEFAULT_TEXT + "{aarstall}", map);
	}

	@Test
	public void shouldDoNothingWhenNoFletteparameters() throws Exception {
		String varselUrl = fletter.weaveVarselUrl(DEFAULT_URL, Maps.newHashMap());

		assertThat(varselUrl, equalTo(DEFAULT_URL));
	}

	@Test
	public void shouldFletteUrl() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");
		map.put("param2", "p2");

		String varselUrl = fletter.weaveVarselUrl("http://nav.no/{param1}/{param2}", map);

		assertThat(varselUrl, equalTo("http://nav.no/p1/p2"));
		assertThat(map.size(), is(0));
	}

	@Test
	public void shouldDoNothingIfParamIsNull() throws Exception {
		assertThat(fletter.weaveVarselUrl(null, Maps.newHashMap()), nullValue());
	}

	@Test
	public void shouldUseFasitPropertyWhenVarselUrlContains$navnobaseurl$() throws Exception {
		String postfix = "/din-innboks";
		String prefix = "prefix";

		String varselUrl = fletter.weaveVarselUrl(prefix + "$navnobaseurl$" + postfix, Maps.newHashMap());

		assertThat(varselUrl, equalTo(prefix + URL_FROM_FASIT + postfix));
	}

	@Test
	public void shouldThrowIfMissingParameterInVarselUrl() throws Exception {
		expectedException.expect(FletteparameterMissingException.class);
		expectedException.expectMessage("missing: param2");

		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");

		fletter.weaveVarselUrl("http://nav.no/{param1}/{param2}", map);
	}

	@Test
	public void shouldConsumeInputMap() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");
		map.put("param2", "p2");

		fletter.weaveVarselUrl("http://nav.no/{param1}/", map);

		assertThat(map.size(), equalTo(1));
		assertThat(map.get("param2"), equalTo("p2"));
	}
}