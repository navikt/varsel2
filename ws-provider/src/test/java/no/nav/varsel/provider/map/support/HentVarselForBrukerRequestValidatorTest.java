package no.nav.varsel.provider.map.support;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.feil.UgydigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Unit test for HentVarselForBrukerRequestValidator
 *
 * @author Lars Aune
 */
public class HentVarselForBrukerRequestValidatorTest {

	public static final String UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE = "Ugyldig bruk av DatoFom og DatoTom";
	public static final String PÅKREVD_INPUTPARAMETER_ER_IKKE_SATT_MESSAGE = "Påkrevd inputparameter er ikke satt";
	public static final String INPUTPARAMETER_BRUKER_MANGLER_FEILAARSAK = "Inputparameter Bruker mangler";
	public static final String PERIODENS_DATO_FOM_KAN_IKKE_VÆRE_SENERE_ENN_PERIODENS_DATO_TOM_FEILAARSAK = "Periodens DatoFom kan ikke være senere enn periodens DatoTom";
	public static final String PERIODENS_DATO_TOM_KAN_IKKE_VÆRE_SENERE_ENN_DAGENS_DATO_FEILAARSAK = "Periodens DatoTom kan ikke være senere enn dagens dato";
	private HentVarselForBrukerRequestValidator validator = new HentVarselForBrukerRequestValidator();

	private AktoerId aktoer;
	private XMLGregorianCalendar tenDaysAgo;
	private XMLGregorianCalendar twentyDaysAgo;
	private XMLGregorianCalendar tenDaysIntoTheFuture;

	@Before
	public void onSetup() throws DatatypeConfigurationException {
		aktoer = new AktoerId();
		aktoer.setAktoerId("AKTOER_ID");

		tenDaysAgo = TestdataUtil.getXMLGregorianCalendar(-10);
		twentyDaysAgo = TestdataUtil.getXMLGregorianCalendar(-20);
		tenDaysIntoTheFuture = TestdataUtil.getXMLGregorianCalendar(10);
	}

	@Test
	public void shouldHaveBruker() throws Exception {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		request.setBruker(null);
		try {
			validator.validate(request);
			fail();
		} catch(HentVarselForBrukerUgyldigInput hvfbui) {
			assertThat(hvfbui.getMessage(), is(PÅKREVD_INPUTPARAMETER_ER_IKKE_SATT_MESSAGE));
			UgydigInput faultInfo = hvfbui.getFaultInfo();
			assertThat(faultInfo.getFeilmelding(), is(PÅKREVD_INPUTPARAMETER_ER_IKKE_SATT_MESSAGE));
			assertThat(faultInfo.getFeilaarsak(), is(INPUTPARAMETER_BRUKER_MANGLER_FEILAARSAK));
			assertThat(faultInfo.getFeilkilde(), nullValue());
			assertThat(faultInfo.getTidspunkt(), notNullValue());
		}
	}

	@Test
	public void shouldHaveDatoTomLaterOrEqualToFomDatoIfBothAreSet() throws Exception {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		request.setBruker(aktoer);
		Periode periode = new Periode();
		periode.setFom(tenDaysAgo);
		periode.setTom(twentyDaysAgo);
		request.setPeriode(periode);
		try {
			validator.validate(request);
			fail();
		} catch(HentVarselForBrukerUgyldigInput hvfbui) {
			assertThat(hvfbui.getMessage(), is(UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE));
			UgydigInput faultInfo = hvfbui.getFaultInfo();
			assertThat(faultInfo.getFeilmelding(), is(UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE));
			assertThat(faultInfo.getFeilaarsak(), is(PERIODENS_DATO_FOM_KAN_IKKE_VÆRE_SENERE_ENN_PERIODENS_DATO_TOM_FEILAARSAK));
			assertThat(faultInfo.getFeilkilde(), nullValue());
			assertThat(faultInfo.getTidspunkt(), notNullValue());
		}
	}

	@Test
	public void shouldHaveDatoTomAtMaximumNowIfFomDatoIsSet() throws Exception {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		request.setBruker(aktoer);
		Periode periode = new Periode();
		periode.setFom(tenDaysAgo);
		periode.setTom(tenDaysIntoTheFuture);
		request.setPeriode(periode);
		try {
			validator.validate(request);
			fail();
		} catch(HentVarselForBrukerUgyldigInput hvfbui) {
			assertThat(hvfbui.getMessage(), is(UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE));
			UgydigInput faultInfo = hvfbui.getFaultInfo();
			assertThat(faultInfo.getFeilmelding(), is(UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_MESSAGE));
			assertThat(faultInfo.getFeilaarsak(), is(PERIODENS_DATO_TOM_KAN_IKKE_VÆRE_SENERE_ENN_DAGENS_DATO_FEILAARSAK));
			assertThat(faultInfo.getFeilkilde(), nullValue());
			assertThat(faultInfo.getTidspunkt(), notNullValue());
		}
	}

	@Test
	public void shouldValidateWhenCalledWithBrukerAndFomDatoBeforeTomDato() {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		request.setBruker(aktoer);
		Periode periode = new Periode();
		periode.setFom(twentyDaysAgo);
		periode.setTom(tenDaysAgo);
		request.setPeriode(periode);
		try {
			validator.validate(request);
		} catch (Exception e) {
			fail();
		}
	}
}