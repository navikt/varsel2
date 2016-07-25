package no.nav.varsel.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import no.nav.varsel.service.support.exception.FletteparameterMissingException;
import no.nav.varsel.service.support.exception.FletteparameterNotUsedException;
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
	private static final String VARSEL_URL = "http://nav.no";
	private static final String DEFAULT_TEXT = "dette er en tekst om året ";

	private VarselFletter fletter = new VarselFletter();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldFlett() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("navn", NAVN);
		map.put("aarstall", AAR);

		assertThat(fletter.weaveVarsel("dette er en tekst om {navn} i {aarstall}", map, VARSEL_URL),
				is("dette er en tekst om Even Lagsdel i 2016"));
	}

	@Test
	public void shouldThrowIfMissingParameter() throws Exception {
		expectedException.expect(FletteparameterMissingException.class);
		expectedException.expectMessage("missing: navn tema");

		Map<String, String> map = new HashMap<>();
		map.put("aarstall", AAR);

		fletter.weaveVarsel("dette er en tekst om {navn} i {aarstall} angående {tema}", map, VARSEL_URL);
	}

	@Test
	public void shouldThrowIfMissingParameterValueInTekst() throws Exception {
		expectedException.expect(FletteparameterNotUsedException.class);
		expectedException.expectMessage("unused: tema");

		Map<String, String> map = new HashMap<>();
		map.put("aarstall", AAR);
		map.put("tema", "klage");

		fletter.weaveVarsel(DEFAULT_TEXT + "{aarstall}", map, VARSEL_URL);
	}

	@Test
	public void shouldAllowVarselUrlToBeUnused() throws Exception {
		fletter.weaveVarsel("dette er en tekst om året", new HashMap<>(), VARSEL_URL);
	}

	@Test
	public void shouldThrowIfVarselUrlParameterIsMissing() throws Exception {
		expectedException.expect(FletteparameterMissingException.class);
		expectedException.expectMessage("missing: varselUrl");

		fletter.weaveVarsel(DEFAULT_TEXT + "{varselUrl}", new HashMap<>(), null);
	}

	@Test
	public void shouldFletteVarselUrl() throws Exception {
		String varsel = fletter.weaveVarsel(DEFAULT_TEXT + "{varselUrl}", new HashMap<>(), VARSEL_URL);

		assertThat(varsel, equalTo(DEFAULT_TEXT + VARSEL_URL));
	}
}