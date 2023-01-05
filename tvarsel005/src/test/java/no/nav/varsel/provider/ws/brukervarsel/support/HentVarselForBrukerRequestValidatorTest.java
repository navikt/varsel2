package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.feil.UgydigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;

public class HentVarselForBrukerRequestValidatorTest {

	private static final String UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE = "Ugyldig bruk av DatoFom og DatoTom";
	private static final String PÅKREVD_INPUTPARAMETER_ER_IKKE_SATT_MESSAGE = "Påkrevd inputparameter er ikke satt";
	private static final String INPUTPARAMETER_BRUKER_MANGLER_FEILAARSAK = "Inputparameter Bruker mangler";
	private static final String PERIODENS_DATO_FOM_KAN_IKKE_VÆRE_SENERE_ENN_PERIODENS_DATO_TOM_FEILAARSAK = "Periodens DatoFom kan ikke være senere enn periodens DatoTom";
	private static final String PERIODENS_DATO_TOM_KAN_IKKE_VÆRE_SENERE_ENN_DAGENS_DATO_FEILAARSAK = "Periodens DatoTom kan ikke være senere enn dagens dato";
	private HentVarselForBrukerRequestValidator validator = new HentVarselForBrukerRequestValidator();

	private AktoerId aktoer;
	private XMLGregorianCalendar tenDaysAgo;
	private XMLGregorianCalendar twentyDaysAgo;
	private XMLGregorianCalendar tenDaysIntoTheFuture;

	@BeforeEach
	public void onSetup() {
		aktoer = new AktoerId();
		aktoer.setAktoerId("AKTOER_ID");

		tenDaysAgo = TestdataUtil.getXMLGregorianCalendar(-10);
		twentyDaysAgo = TestdataUtil.getXMLGregorianCalendar(-20);
		tenDaysIntoTheFuture = TestdataUtil.getXMLGregorianCalendar(10);
	}

	@Test
	public void shouldHaveBruker() {
		HentVarselForBrukerRequest request = createRequest(null, tenDaysAgo, tenDaysIntoTheFuture);
		validate(request, PÅKREVD_INPUTPARAMETER_ER_IKKE_SATT_MESSAGE, INPUTPARAMETER_BRUKER_MANGLER_FEILAARSAK);
	}

	@Test
	public void shouldHaveDatoTomLaterOrEqualToFomDatoIfBothAreSet() {
		HentVarselForBrukerRequest request = createRequest(aktoer, tenDaysAgo, twentyDaysAgo);
		validate(request, UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE, PERIODENS_DATO_FOM_KAN_IKKE_VÆRE_SENERE_ENN_PERIODENS_DATO_TOM_FEILAARSAK);
	}

	@Test
	public void shouldHaveDatoTomAtMaximumNowIfFomDatoIsSet() {
		HentVarselForBrukerRequest request = createRequest(aktoer, tenDaysAgo, tenDaysIntoTheFuture);
		validate(request, UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE, PERIODENS_DATO_TOM_KAN_IKKE_VÆRE_SENERE_ENN_DAGENS_DATO_FEILAARSAK);
	}

	@Test
	public void shouldValidateWhenCalledWithBrukerAndFomDatoBeforeTomDato() throws HentVarselForBrukerUgyldigInput {
		HentVarselForBrukerRequest request = createRequest(aktoer, twentyDaysAgo, tenDaysAgo);
		validator.validate(request);
	}

	@Test
	public void shouldValidateWhenPeriodeNotSet() throws HentVarselForBrukerUgyldigInput {
		HentVarselForBrukerRequest request = createRequest(aktoer);
		validator.validate(request);
	}

	private void validate(HentVarselForBrukerRequest request, String message, String feilaarsak) {
		try {
			validator.validate(request);
			fail();
		} catch (HentVarselForBrukerUgyldigInput hvfbui) {
			assertThat(hvfbui.getMessage(), is(message));
			UgydigInput faultInfo = hvfbui.getFaultInfo();
			assertThat(faultInfo.getFeilmelding(), is(message));
			assertThat(faultInfo.getFeilaarsak(), is(feilaarsak));
			assertThat(faultInfo.getFeilkilde(), nullValue());
			assertThat(faultInfo.getTidspunkt(), notNullValue());
		}
	}

	private HentVarselForBrukerRequest createRequest(AktoerId aktoer, XMLGregorianCalendar tenDaysAgo, XMLGregorianCalendar tenDaysIntoTheFuture) {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		request.setBruker(aktoer);
		Periode periode = new Periode();
		periode.setFom(tenDaysAgo);
		periode.setTom(tenDaysIntoTheFuture);
		request.setPeriode(periode);
		return request;
	}

	private HentVarselForBrukerRequest createRequest(AktoerId aktoer) {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		request.setBruker(aktoer);
		return request;
	}
}