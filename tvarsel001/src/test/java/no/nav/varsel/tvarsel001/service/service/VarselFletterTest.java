package no.nav.varsel.tvarsel001.service.service;

import com.google.common.collect.Maps;
import no.nav.varsel.exception.functional.FletteparameterMissingException;
import no.nav.varsel.exception.functional.InvalidDateTimeFormatException;
import no.nav.varsel.tvarsel001.service.service.VarselFletter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VarselFletterTest {

	private static final String NAVN = "Even Lagsdel";
	private static final String AAR = "2016";

	private static final String DEFAULT_URL = "http://nav.no";
	private static final String URL_FROM_FASIT = "http://fasit.adeo.no";
	private static final String TIME = "2015-07-03T14:30:00";

	private VarselFletter fletter = new VarselFletter();

	@BeforeEach
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
	public void shouldFletteSameParameterMultipleTimes() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("navn", NAVN);
		map.put("aarstall", AAR);

		assertThat(fletter.weaveText("{navn} og {navn}", map), is(NAVN + " og " + NAVN));
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
		Map<String, String> map = new HashMap<>();
		map.put("tid", TIME);

		Executable executable = () -> fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.PP.yyyy}", map);
		Exception exception = Assertions.assertThrows(InvalidDateTimeFormatException.class, executable);
		assertTrue(exception.getMessage().contains("Invalid format for dateTime pattern for varsel, parameter tid dd.PP.yyyy"));
	}

	@Test
	public void shouldThrowIfInvalidTime() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("tid", "2015-07-T14:30:00");
		Executable executable = () -> fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.PP.yyyy}", map);

		Exception exception = Assertions.assertThrows(InvalidDateTimeFormatException.class, executable);
		assertTrue(exception.getMessage().contains("Invalid format for dateTime for varsel, parameter tid 2015-07-T14:30:00"));
	}

	@Test
	public void shouldThrowIfMissingParameter() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("aarstall", AAR);

		Executable executable = () -> fletter.weaveText("dette er en tekst om {navn} i {aarstall} angående {tema} {tid:dd.MM.yyyy}", map);
		Exception exception = Assertions.assertThrows(FletteparameterMissingException.class, executable);
		assertTrue(exception.getMessage().contains("missing: navn tema tid"));
	}

	@Test
	public void shouldReturnNullWhenTextIsNull() throws Exception {
		String varselUrl = fletter.weaveText(null, Maps.newHashMap());

		assertThat(varselUrl, nullValue());
	}

	@Test
	public void shouldWorkWithLongFlettedata() {
		String template = "Hei! Du har fått et spørsmål fra NAV. Du får se spørsmålet ved å logge deg inn i " +
				"Innboks på Ditt NAV eller følge denne lenken {url}.\n\nVennlig hilsen NAV";

		String excpectedResult = "Hei! Du har fått et spørsmål fra NAV. Du får se spørsmålet ved å logge deg inn i " +
				"Innboks på Ditt NAV eller følge denne lenken https://somesubdomain.nav.no/mininnboks/traad/10005WTVQ.\n\nVennlig hilsen NAV";

		Map<String, String> map = new HashMap<>();
		map.put("url", "https://somesubdomain.nav.no/mininnboks/traad/10005WTVQ");

		assertThat(fletter.weaveText(template, map),
				is(excpectedResult));
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
		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");

		Executable executable = () -> fletter.weaveText("http://nav.no/{param1}/{param2}", map);
		Exception exception = Assertions.assertThrows(FletteparameterMissingException.class, executable);
		assertTrue(exception.getMessage().contains("missing: param2"));
	}

	@Test
	public void testArrayOutOfBoundsExc_PKFEIL20151() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("sted", "NAV Bærum - Løkketangen");
		map.put("tid", "2016-10-20T10:00:00");

		fletter.weaveText("Hei! Du har et møte i regi av NAV på {sted} {tid:dd.MM.yyyy} klokken {tid:HH:mm}\n" +
				"Vennlig hilsen NAV", map);
		fletter.weaveText("Hei! Dette er en beskjed om at du har et møte på {sted} {tid:dd:MM:yyyy} klokken {tid:HH:mm}\n" +
				"Vennlig hilsen NAV", map);
		fletter.weaveText("Hei! Dette er en beskjed om at du har et møte på {sted} {tid:dd:MM:yyyy} klokken {tid:HH:mm}", map);
	}
}