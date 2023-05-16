package no.nav.varsel.jms.consumer.tvarsel006.support;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSAktoer;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSAktoerId;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSKommunikasjonskanaler;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSKontaktinformasjon;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSOrganisasjon;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSParameter;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSPerson;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSServicemeldingMedKontaktinformasjon;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.service.to.BestillVarselTo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.varsel.Utils.formatDateTime;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BestillServicemeldingMedKontaktInfoMapperTest {

	public static final String KEY = "mottaker";
	public static final String VAL = "val";
	public static final String AKTOER_ID = "aktoerId"; // aktoerId 1234567890123
	public static final String PERSON_IDENT = "1234567890123";
	public static final String VARSELTYPE_ID = "varseltypeId";
	public static final LocalDateTime UTLOEPS_TIDSPUNKT = LocalDateTime.now().plusHours(1);
	public static final String ORGNUMMER = "orgnummer";
	public static final String EPOST = "er@mocked.data";
	public static final String TLF = "11223344";

	private final BestillServicemeldingMedKontaktInfoMapper mapper = new BestillServicemeldingMedKontaktInfoMapper();

	@Test
	public void shouldMap() {
		BestillVarselTo to = mapper.map(createServicemeldingMedKontaktinformasjon());

		assertThat(to.getAktoerId(), is(AKTOER_ID));
		assertThat(to.getOrgNr(), is(ORGNUMMER));
		assertThat(to.getEpost(), is(EPOST));
		assertThat(to.getMobiltelefonnummer(), is(TLF));
		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(to.getParameters().keySet(), hasSize(1));
		assertThat(to.getParameters().get(KEY), Matchers.is(VAL));
		assertThat(formatDateTime(to.getUtloepstidspunkt()), is(formatDateTime((UTLOEPS_TIDSPUNKT))));
	}

	@Test
	public void shouldHandleNullOnOptional() {
		WSServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();
		varsel.getParameterListe().clear();
		varsel.setUtloepstidspunkt(null);

		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getUtloepstidspunkt(), nullValue());
		assertThat(to.getParameters().size(), is(0));
	}

	@Test
	public void shouldMapPersonIdent() {
		WSServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();
		varsel.setMottaker(createPerson());

		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getAktoerId(), is(nullValue()));
		assertThat(to.getPersonIdent(), is(PERSON_IDENT));
	}

	@Test
	public void shouldHandleOrganisasjonNull() {
		WSServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();
		varsel.setTilhoerendeOrganisasjon(null);

		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getOrgNr(), nullValue());
	}

	@Test
	public void shouldThrowIfKontaktInfoKanalIsDittNav() {
		WSServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();

		WSKontaktinformasjon kontaktinformasjon = new WSKontaktinformasjon();
		kontaktinformasjon.setKanal(createKommunkasjonskanaler(DITT_NAV));
		varsel.getKontaktinformasjonListe().add(kontaktinformasjon);

		Exception e = assertThrows(IllegalArgumentException.class, () -> mapper.map(varsel));
		assertEquals("Ugyldig kommunikasjonskanal=DITT_NAV", e.getMessage());
	}

	@Test
	public void shouldThrowIllegalArgumentIfInfoKanalNull() {
		WSServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();

		WSKontaktinformasjon kontaktinformasjon = new WSKontaktinformasjon();
		kontaktinformasjon.setKanal(null);
		varsel.getKontaktinformasjonListe().add(kontaktinformasjon);

		Exception e = assertThrows(IllegalArgumentException.class, () -> mapper.map(varsel));
		assertEquals("Ugyldig kommunikasjonskanal=null", e.getMessage());
	}

	public static WSServicemeldingMedKontaktinformasjon createServicemeldingMedKontaktinformasjon() {
		WSServicemeldingMedKontaktinformasjon varsel = new WSServicemeldingMedKontaktinformasjon();
		varsel.setMottaker(createAktoerId());
		varsel.setTilhoerendeOrganisasjon(createOrganisasjon());
		varsel.setVarseltypeId(VARSELTYPE_ID);
		varsel.getParameterListe().add(createParameter());
		varsel.setUtloepstidspunkt(toXmlGregorianCalendar(UTLOEPS_TIDSPUNKT));
		varsel.getKontaktinformasjonListe().add(createKontaktInfoSms());
		varsel.getKontaktinformasjonListe().add(createKontaktInfoEpost());

		return varsel;
	}

	private static WSOrganisasjon createOrganisasjon() {
		WSOrganisasjon organisasjon = new WSOrganisasjon();
		organisasjon.setOrgnummer(ORGNUMMER);

		return organisasjon;
	}

	private static WSParameter createParameter() {
		WSParameter parameter = new WSParameter();
		parameter.setKey(KEY);
		parameter.setValue(VAL);

		return parameter;
	}

	private static WSKontaktinformasjon createKontaktInfoSms() {
		WSKontaktinformasjon kontaktinformasjon = new WSKontaktinformasjon();
		kontaktinformasjon.setKontaktinformasjon(TLF);
		kontaktinformasjon.setKanal(createKommunkasjonskanaler(KanalCode.SMS));

		return kontaktinformasjon;
	}

	private static WSKontaktinformasjon createKontaktInfoEpost() {
		WSKontaktinformasjon kontaktinformasjon = new WSKontaktinformasjon();
		kontaktinformasjon.setKontaktinformasjon(EPOST);
		kontaktinformasjon.setKanal(createKommunkasjonskanaler(KanalCode.EPOST));

		return kontaktinformasjon;
	}

	private static WSKommunikasjonskanaler createKommunkasjonskanaler(KanalCode kanalCode) {
		WSKommunikasjonskanaler kommunikasjonskanaler = new WSKommunikasjonskanaler();
		kommunikasjonskanaler.setValue(kanalCode.name());

		return kommunikasjonskanaler;
	}

	private static WSAktoer createAktoerId() {
		WSAktoerId aktoerId = new WSAktoerId();
		aktoerId.setAktoerId(AKTOER_ID);

		return aktoerId;
	}

	private static WSAktoer createPerson() {
		WSPerson person = new WSPerson();
		person.setIdent(PERSON_IDENT);

		return person;
	}
}