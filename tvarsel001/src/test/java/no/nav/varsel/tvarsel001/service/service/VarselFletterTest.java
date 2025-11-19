package no.nav.varsel.tvarsel001.service.service;

import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.exception.functional.FletteparameterMissingException;
import no.nav.varsel.exception.functional.InvalidDateTimeFormatException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class VarselFletterTest {

	private static final String NAVN = "Even Lagsdel";
	private static final String AAR = "2016";

	private static final String DEFAULT_URL = "http://nav.no";
	private static final String URL_FROM_FASIT = "http://fasit.adeo.no";
	private static final String TIME = "2015-07-03T14:30:00";

	private final VarselFletter fletter;

	public VarselFletterTest() {
		VarselProperties varselProperties = new VarselProperties();
		varselProperties.setUrl(URL_FROM_FASIT);
		this.fletter = new VarselFletter(varselProperties);
	}

	@Test
	public void shouldFlett() {
		Map<String, String> map = new HashMap<>();
		map.put("navn", NAVN);
		map.put("aarstall", AAR);

		String flettetTekst = fletter.weaveText("dette er en tekst om {navn} i {aarstall}", map);

		assertThat(flettetTekst).isEqualTo("dette er en tekst om Even Lagsdel i 2016");
	}

	@Test
	public void shouldFletteSameParameterMultipleTimes() {
		Map<String, String> map = new HashMap<>();
		map.put("navn", NAVN);
		map.put("aarstall", AAR);

		String flettetTekst = fletter.weaveText("{navn} og {navn}", map);

		assertThat(flettetTekst).isEqualTo(NAVN + " og " + NAVN);
	}

	@Test
	public void shouldFlettTimeParameter() {
		Map<String, String> map = new HashMap<>();
		map.put("tid", TIME);

		String flettetTekst = fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.MM.yyyy} klokken {tid:HH:mm}.", map);

		assertThat(flettetTekst).isEqualTo("Dette er en beskjed om at du har et møte 03.07.2015 klokken 14:30.");
	}

	@Test
	public void shouldThrowIfInvalidTimePattern() {
		Map<String, String> map = new HashMap<>();
		map.put("tid", TIME);

		assertThatExceptionOfType(InvalidDateTimeFormatException.class)
				.isThrownBy(() -> fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.PP.yyyy}", map))
				.withMessage("Invalid format for dateTime pattern for varsel, parameter tid dd.PP.yyyy");
	}

	@Test
	public void shouldThrowIfInvalidTime() {
		Map<String, String> map = new HashMap<>();
		map.put("tid", "2015-07-T14:30:00");

		assertThatExceptionOfType(InvalidDateTimeFormatException.class)
				.isThrownBy(() -> fletter.weaveText("Dette er en beskjed om at du har et møte {tid:dd.PP.yyyy}", map))
				.withMessage("Invalid format for dateTime for varsel, parameter tid 2015-07-T14:30:00");
	}

	@Test
	public void shouldThrowIfMissingParameter() {
		Map<String, String> map = new HashMap<>();
		map.put("aarstall", AAR);

		assertThatExceptionOfType(FletteparameterMissingException.class)
				.isThrownBy(() -> fletter.weaveText("dette er en tekst om {navn} i {aarstall} angående {tema} {tid:dd.MM.yyyy}", map))
				.withMessage("Not all parameters given for varsel, missing: navn tema tid");
	}

	@Test
	public void shouldReturnNullWhenTextIsNull() {
		String flettetTekst = fletter.weaveText(null, emptyMap());

		assertThat(flettetTekst).isNull();
	}

	@Test
	public void shouldWorkWithLongFlettedata() {
		String template = """
				Hei! Du har fått et spørsmål fra NAV. Du får se spørsmålet ved å logge deg inn i Innboks på Ditt NAV eller følge denne lenken {url}.

				Vennlig hilsen NAV""";

		Map<String, String> map = new HashMap<>();
		map.put("url", "https://somesubdomain.nav.no/mininnboks/traad/10005WTVQ");

		String flettetTekst = fletter.weaveText(template, map);

		assertThat(flettetTekst).isEqualTo("""
				Hei! Du har fått et spørsmål fra NAV. Du får se spørsmålet ved å logge deg inn i Innboks på Ditt NAV eller følge denne lenken https://somesubdomain.nav.no/mininnboks/traad/10005WTVQ.

				Vennlig hilsen NAV"""
		);
	}

	@Test
	public void shouldDoNothingWhenNoFletteparameters() {
		String flettetTekst = fletter.weaveText(DEFAULT_URL, emptyMap());

		assertThat(flettetTekst).isEqualTo(DEFAULT_URL);
	}

	@Test
	public void shouldFletteUrl() {
		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");
		map.put("param2", "p2");

		String flettetTekst = fletter.weaveText("http://nav.no/{param1}/{param2}", map);

		assertThat(flettetTekst).isEqualTo("http://nav.no/p1/p2");
	}

	@Test
	public void shouldUseFasitPropertyWhenVarselUrlContains$navnobaseurl$() {
		String postfix = "/din-innboks";
		String prefix = "prefix";

		String flettetTekst = fletter.weaveText(prefix + "$navnobaseurl$" + postfix, emptyMap());

		assertThat(flettetTekst).isEqualTo(prefix + URL_FROM_FASIT + postfix);
	}

	@Test
	public void shouldThrowIfMissingParameterInVarselUrl() {
		Map<String, String> map = new HashMap<>();
		map.put("param1", "p1");

		assertThatExceptionOfType(FletteparameterMissingException.class)
				.isThrownBy(() -> fletter.weaveText("http://nav.no/{param1}/{param2}", map))
				.withMessage("Not all parameters given for varsel, missing: param2");
	}

	@Test
	public void testArrayOutOfBoundsExc_PKFEIL20151() {
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