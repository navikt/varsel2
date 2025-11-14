package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.tms.varsel.builder.BuilderEnvironment;
import no.nav.varsel.exception.functional.ServicemeldingMappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.SIKKERHETSNIVAA_MINID;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.UGYLDIG_VARSEL_URL;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTEKST_DITT_NAV;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTEKST_EPOST;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTEKST_SMS;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSELTITTEL_EPOST;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.VARSEL_URL;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrukernotifikasjonMapperTest {

	private static final String JSON_CONTEXT = "\"%s\":\"%s\"";
	private static final String EKSTERN_VARSEL_JSON_KEY = "eksternVarsling";
	private static final String EPOST_TEKST_JSON_KEY = "epostVarslingstekst";
	private static final String EPOST_TITTEL_JSON_KEY = "epostVarslingstittel";
	private static final String SMS_TEKST_JSON_KEY = "smsVarslingstekst";
	private static final String DITT_NAV_TEKST_JSON_KEY = "tekst";
	private static final String DITT_NAV_URL_JSON_KEY = "link";
	private static final String SIKKERHETSNIVAA_JSON_KEY = "sensitivitet";


	@BeforeEach
	void setUp() {
		BuilderEnvironment.extend(Map.of(
				"NAIS_APP_NAME", "varsel2",
				"NAIS_NAMESPACE", "teamdokumenthandtering",
				"NAIS_CLUSTER_NAME", "test"
		));
	}

	@Test
	public void shouldMapBeskjed() {
		var marshalledOpprettVarsel = BrukernotifikasjonMapper.mapAndMarshalOpprettVarsel(createFrom(DITT_NAV, SMS, EPOST));

		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(DITT_NAV_TEKST_JSON_KEY, VARSELTEKST_DITT_NAV));
		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(DITT_NAV_URL_JSON_KEY, VARSEL_URL));
		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(SIKKERHETSNIVAA_JSON_KEY, SIKKERHETSNIVAA_MINID));
		assertThat(marshalledOpprettVarsel).contains(EKSTERN_VARSEL_JSON_KEY);

		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(SMS_TEKST_JSON_KEY, VARSELTEKST_SMS));
		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(EPOST_TEKST_JSON_KEY, VARSELTEKST_EPOST));
		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(EPOST_TITTEL_JSON_KEY, VARSELTITTEL_EPOST));
	}

	@Test
	void shouldMapBeskjedUtenSms() {
		var marshalledOpprettVarsel = BrukernotifikasjonMapper.mapAndMarshalOpprettVarsel(createFrom(DITT_NAV, EPOST));

		assertThat(marshalledOpprettVarsel).doesNotContain(SMS_TEKST_JSON_KEY);
	}

	@Test
	void shouldMapBeskjedUtenEpost() {
		var marshalledOpprettVarsel = BrukernotifikasjonMapper.mapAndMarshalOpprettVarsel(createFrom(DITT_NAV, SMS));

		assertThat(marshalledOpprettVarsel).doesNotContain(EPOST_TEKST_JSON_KEY);
		assertThat(marshalledOpprettVarsel).doesNotContain(EPOST_TITTEL_JSON_KEY);
	}

	@Test
	void shouldMapBeskjedUtenSmsOgEpost() {
		var marshalledOpprettVarsel = BrukernotifikasjonMapper.mapAndMarshalOpprettVarsel(createFrom(DITT_NAV));

		assertThat(marshalledOpprettVarsel).doesNotContain(EKSTERN_VARSEL_JSON_KEY);
		assertThat(marshalledOpprettVarsel).doesNotContain(SMS_TEKST_JSON_KEY);
		assertThat(marshalledOpprettVarsel).doesNotContain(EPOST_TEKST_JSON_KEY);
		assertThat(marshalledOpprettVarsel).doesNotContain(EPOST_TITTEL_JSON_KEY);
	}

	@Test
	public void shouldMapBeskjedUtenUrl() {
		var marshalledOpprettVarsel = BrukernotifikasjonMapper.mapAndMarshalOpprettVarsel(createFrom((String) null, DITT_NAV));

		assertThat(marshalledOpprettVarsel).doesNotContain(EKSTERN_VARSEL_JSON_KEY);
		assertThat(marshalledOpprettVarsel).doesNotContain(DITT_NAV_URL_JSON_KEY);
		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(DITT_NAV_TEKST_JSON_KEY, VARSELTEKST_DITT_NAV));
		assertThat(marshalledOpprettVarsel).contains(JSON_CONTEXT.formatted(SIKKERHETSNIVAA_JSON_KEY, SIKKERHETSNIVAA_MINID));
	}

	@Test
	public void shouldFailOnInvalidVarselUrl() {
		Exception e = assertThrows(ServicemeldingMappingException.class, () -> BrukernotifikasjonMapper.mapAndMarshalOpprettVarsel(createFrom(UGYLDIG_VARSEL_URL, DITT_NAV)));
		assertThat(e.getMessage()).contains("Feil ved validering av varsel-action: Link må være gyldig URL og maks 200 tegn");
	}
}
