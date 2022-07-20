package no.nav.varsel.consumer.dkif.support;

import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static no.nav.varsel.consumer.dkif.DigitalKontaktInfoResponse.DigitalKontaktinfo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link HentDigitalKontaktinformasjonMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HentDigitalKontaktinformasjonMapperTest {

	public static final String ID = "id";
	public static final boolean RESERVASJON = false;
	public static final String EPOSTADRESSE = "er@mocked.data";
	public static final String MOBILTELEFONNUMMER = "54621378";

	public static final LocalTime MIDNATT = LocalTime.of(0, 0, 0);
	public static final ZonedDateTime EPOST_OPPDATERT = ZonedDateTime.of(LocalDateTime.of(LocalDate.of(2020, 1, 1), MIDNATT), ZoneOffset.systemDefault());
	public static final ZonedDateTime EPOST_VERIFISERT = ZonedDateTime.of(LocalDateTime.of(LocalDate.of(2022, 1, 1), MIDNATT), ZoneOffset.systemDefault());
	public static final ZonedDateTime MOB_OPPDATERT = ZonedDateTime.of(LocalDateTime.of(LocalDate.of(2020, 1, 1), MIDNATT), ZoneOffset.systemDefault());
	public static final ZonedDateTime MOB_VERIFISERT = ZonedDateTime.of(LocalDateTime.of(LocalDate.of(2022, 1, 1), MIDNATT), ZoneOffset.systemDefault());

	private final HentDigitalKontaktinformasjonMapper mapper = new HentDigitalKontaktinformasjonMapper();

	@Test
	public void shouldMapResponse() {
		KontaktregisterTo map = mapper.map(createResponse());
		assertThat(map.isReservasjon(), is(RESERVASJON));
		assertThat(map.getEpostadresse(), is(EPOSTADRESSE));
		assertThat(map.getEpostSistOppdatert(), is(EPOST_OPPDATERT.toLocalDateTime()));
		assertThat(map.getEpostSistVerifisert(), is(EPOST_VERIFISERT.toLocalDateTime()));
		assertThat(map.getMobiltelefonnummer(), is(MOBILTELEFONNUMMER));
		assertThat(map.getMobiltelefonSistOppdatert(), is(MOB_OPPDATERT.toLocalDateTime()));
		assertThat(map.getMobiltelefonSistVerifisert(), is(MOB_VERIFISERT.toLocalDateTime()));
	}

	@Test
	public void shouldMapResponseNullDate() {
		DigitalKontaktinfo response = createResponse();
		response.setMobiltelefonnummerVerifisert(null);
		KontaktregisterTo map = mapper.map(response);
		assertThat(map.getMobiltelefonSistVerifisert(), nullValue());
	}
	@Test
	public void shouldMapResponseNullEpostMobil() {
		DigitalKontaktinfo response = createResponse();
		response.setEpostadresse(null);
		response.setMobiltelefonnummer(null);
		KontaktregisterTo map = mapper.map(response);
		assertThat(map.getMobiltelefonnummer(), nullValue());
		assertThat(map.getEpostadresse(), nullValue());
	}

	@Test
	public void shoulRemoveWhitespaceFromEpostadresseAndMobiltelefonnummer() {
		DigitalKontaktinfo response = createResponse();
		KontaktregisterTo map = mapper.map(response);
		assertEquals(map.getMobiltelefonnummer(), MOBILTELEFONNUMMER);
		assertEquals(map.getEpostadresse(), EPOSTADRESSE);
	}

	public static DigitalKontaktinfo createResponse() {
		return DigitalKontaktinfo.builder()
				.reservert(RESERVASJON)
				.epostadresse(EPOSTADRESSE)
				.mobiltelefonnummer(MOBILTELEFONNUMMER)
				.epostadresseOppdatert(EPOST_OPPDATERT)
				.epostadresseVerifisert(EPOST_VERIFISERT)
				.mobiltelefonnummerOppdatert(MOB_OPPDATERT)
				.mobiltelefonnummerVerifisert(MOB_VERIFISERT)
				.build();
	}
}