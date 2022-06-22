package no.nav.varsel.consumer.dkif.support;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.Epostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.Mobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.varsel.consumer.dkif.DigitalKontaktInfoResponse;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

import static no.nav.varsel.consumer.dkif.DigitalKontaktInfoResponse.*;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
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

	public static final XMLGregorianCalendar EPOST_OPPDATERT;
	public static final XMLGregorianCalendar EPOST_VERIFISERT;
	public static final XMLGregorianCalendar MOB_OPPDATERT;
	public static final XMLGregorianCalendar MOB_VERIFISERT;

	private final HentDigitalKontaktinformasjonMapper mapper = new HentDigitalKontaktinformasjonMapper();

	static {
		EPOST_OPPDATERT = createDate(1);
		EPOST_VERIFISERT = createDate(2);
		MOB_OPPDATERT = createDate(3);
		MOB_VERIFISERT = createDate(4);
	}

	@Test
	public void shouldMapResponse() {
		KontaktregisterTo map = mapper.map(createResponse());
		assertThat(map.isReservasjon(), is(RESERVASJON));
		assertThat(map.getEpostadresse(), is(EPOSTADRESSE));
		assertThat(map.getMobiltelefonnummer(), is(MOBILTELEFONNUMMER));
	}

	@Test
	public void shouldMapBools() {
		assertThat(mapper.mapStringToBool("true"), is(true));
		assertThat(mapper.mapStringToBool("false"), is(false));
		assertThat(mapper.mapStringToBool("JA"), is(true));
		assertThat(mapper.mapStringToBool("NEI"), is(false));
		assertThat(mapper.mapStringToBool(""), is(true));
		assertThat(mapper.mapStringToBool(null), is(true));
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
		
		Mobiltelefonnummer mobiltelefonnummer = new Mobiltelefonnummer();
		mobiltelefonnummer.setValue(" " + MOBILTELEFONNUMMER + " ");
		response.setMobiltelefonnummer(mobiltelefonnummer.getValue());
		
		Epostadresse epostadresse = new Epostadresse();
		epostadresse.setValue(" " + EPOSTADRESSE + " ");
		response.setEpostadresse(epostadresse.getValue());
		
		KontaktregisterTo map = mapper.map(response);
		assertEquals(map.getMobiltelefonnummer(), MOBILTELEFONNUMMER);
		assertEquals(map.getEpostadresse(), EPOSTADRESSE);
	}

	public static DigitalKontaktinfo createResponse() {
		DigitalKontaktinfo kontaktinformasjon = DigitalKontaktinfo.builder()
				.reservert(RESERVASJON)
				.epostadresse(EPOSTADRESSE)
				.mobiltelefonnummer(MOBILTELEFONNUMMER)
				.build();
		return kontaktinformasjon;
	}

	private static XMLGregorianCalendar createDate(int i) {
		return toXmlGregorianCalendar(LocalDateTime.now().minusHours(i));
	}
}