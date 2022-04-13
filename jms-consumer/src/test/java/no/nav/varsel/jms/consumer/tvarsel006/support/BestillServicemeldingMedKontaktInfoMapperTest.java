package no.nav.varsel.jms.consumer.tvarsel006.support;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Aktoer;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.AktoerId;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Kommunikasjonskanaler;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Kontaktinformasjon;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Organisasjon;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Parameter;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Person;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ServicemeldingMedKontaktinformasjon;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.service.to.BestillVarselTo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.varsel.Utils.formatDateTime;
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
		ServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();
		varsel.getParameterListe().clear();
		varsel.setUtloepstidspunkt(null);

		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getUtloepstidspunkt(), nullValue());
		assertThat(to.getParameters().size(), is(0));
	}

	@Test
	public void shouldMapPersonIdent() {
		ServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();
		varsel.setMottaker(createPerson());

		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getAktoerId(), is(nullValue()));
		assertThat(to.getPersonIdent(), is(PERSON_IDENT));
	}

	@Test
	public void shouldHandleOrganisasjonNull() {
		ServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();
		varsel.setTilhoerendeOrganisasjon(null);

		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getOrgNr(), nullValue());
	}

	@Test
	public void shouldThrowIfKontaktInfoKanalIsDittNav() {
		ServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();

		Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
		kontaktinformasjon.setKanal(createKommunkasjonskanaler(KanalCode.DITT_NAV));
		varsel.getKontaktinformasjonListe().add(kontaktinformasjon);

		Exception e = assertThrows(IllegalArgumentException.class, () -> mapper.map(varsel));
		assertEquals("Invalid kommunikajsonskanal=DITT_NAV", e.getMessage());
	}

	@Test
	public void shouldThrowIllegalArgumentIfInfoKanalNull() {
		ServicemeldingMedKontaktinformasjon varsel = createServicemeldingMedKontaktinformasjon();

		Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
		kontaktinformasjon.setKanal(null);
		varsel.getKontaktinformasjonListe().add(kontaktinformasjon);

		Exception e = assertThrows(IllegalArgumentException.class, () -> mapper.map(varsel));
		assertEquals("Invalid kommunikajsonskanal=null", e.getMessage());
	}

	public static ServicemeldingMedKontaktinformasjon createServicemeldingMedKontaktinformasjon() {
		ServicemeldingMedKontaktinformasjon varsel = new ServicemeldingMedKontaktinformasjon();
		varsel.setMottaker(createAktoerId());
		varsel.setTilhoerendeOrganisasjon(createOrganisasjon());
		varsel.setVarseltypeId(VARSELTYPE_ID);
		varsel.getParameterListe().add(createParameter());
		varsel.setUtloepstidspunkt(toXmlGregorianCalendar(UTLOEPS_TIDSPUNKT));
		varsel.getKontaktinformasjonListe().add(createKontaktInfoSms());
		varsel.getKontaktinformasjonListe().add(createKontaktInfoEpost());
		return varsel;
	}

	private static Organisasjon createOrganisasjon() {
		Organisasjon organisasjon = new Organisasjon();
		organisasjon.setOrgnummer(ORGNUMMER);
		return organisasjon;
	}

	private static Parameter createParameter() {
		Parameter parameter = new Parameter();
		parameter.setKey(KEY);
		parameter.setValue(VAL);
		return parameter;
	}

	private static Kontaktinformasjon createKontaktInfoSms() {
		Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
		kontaktinformasjon.setKontaktinformasjon(TLF);
		kontaktinformasjon.setKanal(createKommunkasjonskanaler(KanalCode.SMS));
		return kontaktinformasjon;
	}

	private static Kontaktinformasjon createKontaktInfoEpost() {
		Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
		kontaktinformasjon.setKontaktinformasjon(EPOST);
		kontaktinformasjon.setKanal(createKommunkasjonskanaler(KanalCode.EPOST));
		return kontaktinformasjon;
	}

	private static Kommunikasjonskanaler createKommunkasjonskanaler(KanalCode kanalCode) {
		Kommunikasjonskanaler kommunikasjonskanaler = new Kommunikasjonskanaler();
		kommunikasjonskanaler.setValue(kanalCode.name());
		return kommunikasjonskanaler;
	}

	private static Aktoer createAktoerId() {
		AktoerId aktoerId = new AktoerId();
		aktoerId.setAktoerId(AKTOER_ID);
		return aktoerId;
	}

	private static Aktoer createPerson() {
		Person person = new Person();
		person.setIdent(PERSON_IDENT);
		return person;
	}
}