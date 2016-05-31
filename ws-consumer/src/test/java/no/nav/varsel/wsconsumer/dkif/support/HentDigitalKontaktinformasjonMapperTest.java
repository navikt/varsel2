package no.nav.varsel.wsconsumer.dkif.support;

import static no.nav.varsel.domain.auxillary.XmlGregorianConverter.toLocalDateTime;
import static no.nav.varsel.domain.auxillary.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.Epostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.Kontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.Mobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

/**
 * Unit test for {@link HentDigitalKontaktinformasjonMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HentDigitalKontaktinformasjonMapperTest {

	public static final String ID = "id";
	public static final boolean RESERVASJON = true;
	public static final String RESERVASJON_STRING = String.valueOf(RESERVASJON);
	public static final String EPOSTADRESSE = "er@mocked.data";
	public static final String MOBILTELEFONNUMMER = "54621378";

	public static final XMLGregorianCalendar EPOST_OPPDATERT;
	public static final XMLGregorianCalendar EPOST_VERIFISERT;
	public static final XMLGregorianCalendar MOB_OPPDATERT;
	public static final XMLGregorianCalendar MOB_VERIFISERT;

	private HentDigitalKontaktinformasjonMapper mapper = new HentDigitalKontaktinformasjonMapper();

	static {
		EPOST_OPPDATERT = createDate(1);
		EPOST_VERIFISERT = createDate(2);
		MOB_OPPDATERT = createDate(3);
		MOB_VERIFISERT = createDate(4);
	}

	@Test
	public void shouldMapResponse() throws Exception {
		KontaktregisterTo map = mapper.map(createResponse());
		assertThat(map.getPersonIdent(), is(ID));
		assertThat(map.isReservasjon(), is(RESERVASJON));
		assertThat(map.getEpostadresse(), is(EPOSTADRESSE));
		assertThat(map.getEpostSistOppdatert(), is(toLocalDateTime(EPOST_OPPDATERT)));
		assertThat(map.getEpostSistVerifisert(), is(toLocalDateTime(EPOST_VERIFISERT)));
		assertThat(map.getMobiltelefonnummer(), is(MOBILTELEFONNUMMER));
		assertThat(map.getMobiltelefonSistOppdatert(), is(toLocalDateTime(MOB_OPPDATERT)));
		assertThat(map.getMobiltelefonSistVerifisert(), is(toLocalDateTime(MOB_VERIFISERT)));
	}

	@Test
	public void shouldMapBools() throws Exception {
		assertThat(mapper.mapStringToBool("true"), is(true));
		assertThat(mapper.mapStringToBool("false"), is(false));
		assertThat(mapper.mapStringToBool("JA"), is(true));
		assertThat(mapper.mapStringToBool("NEI"), is(false));
		assertThat(mapper.mapStringToBool(""), is(true));
		assertThat(mapper.mapStringToBool(null), is(true));
	}

	@Test
	public void shouldMapResponseNullEpostMobil() throws Exception {
		HentDigitalKontaktinformasjonResponse response = createResponse();
		response.getDigitalKontaktinformasjon().setEpostadresse(null);
		response.getDigitalKontaktinformasjon().setMobiltelefonnummer(null);
		KontaktregisterTo map = mapper.map(response);
		assertThat(map.getMobiltelefonnummer(), nullValue());
		assertThat(map.getEpostadresse(), nullValue());
	}

	@Test
	public void shouldMapResponseNullDate() throws Exception {
		HentDigitalKontaktinformasjonResponse response = createResponse();
		response.getDigitalKontaktinformasjon().getMobiltelefonnummer()
				.setSistVerifisert(null);
		KontaktregisterTo map = mapper.map(response);
		assertThat(map.getMobiltelefonSistVerifisert(), nullValue());
	}

	public static HentDigitalKontaktinformasjonResponse createResponse() {
		HentDigitalKontaktinformasjonResponse response = new HentDigitalKontaktinformasjonResponse();
		Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
		response.setDigitalKontaktinformasjon(kontaktinformasjon);

		kontaktinformasjon.setPersonident(ID);
		kontaktinformasjon.setReservasjon(RESERVASJON_STRING);
		Epostadresse epostadresse = new Epostadresse();
		epostadresse.setValue(EPOSTADRESSE);
		epostadresse.setSistOppdatert(EPOST_OPPDATERT);
		epostadresse.setSistVerifisert(EPOST_VERIFISERT);
		kontaktinformasjon.setEpostadresse(epostadresse);
		Mobiltelefonnummer mobiltelefonnummer = new Mobiltelefonnummer();
		mobiltelefonnummer.setValue(MOBILTELEFONNUMMER);
		mobiltelefonnummer.setSistOppdatert(MOB_OPPDATERT);
		mobiltelefonnummer.setSistVerifisert(MOB_VERIFISERT);
		kontaktinformasjon.setMobiltelefonnummer(mobiltelefonnummer);

		return response;
	}

	private static XMLGregorianCalendar createDate(int i) {
		return toXmlGregorianCalendar(LocalDateTime.now().minusHours(i));
	}
}