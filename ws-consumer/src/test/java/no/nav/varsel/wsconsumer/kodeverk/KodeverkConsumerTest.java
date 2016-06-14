package no.nav.varsel.wsconsumer.kodeverk;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.wsconsumer.kodeverk.KodeverkConsumer.PERSONIDENTER_KODEVERKSNAVN;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkV2;
import no.nav.tjeneste.virksomhet.kodeverk.v2.ObjectFactory;
import no.nav.tjeneste.virksomhet.kodeverk.v2.feil.KodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.EnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Periode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 *
 * Unit test for {@link KodeverkConsumer}
 *
 * @author lars Aune
 */
@RunWith(MockitoJUnitRunner.class)
public class KodeverkConsumerTest {

	private static final String NON_EXISITING_KODEVERKSNAVN = "nonExisingKodeverksNavn";
	private static final String PERSON_IDENTER_FDAT_KODENAVN = "FDAT";
	private static final String PERSON_IDENTER_FNR_KODENAVN = "FNR";
	private static final String PERSON_IDENTER_BOST_KODENAVN = "BOST";
	private static final String PERSON_IDENTER_DNR_KODENAVN = "DNR";
	private static final String PERSON_IDENTER_NON_EXSISTING_KODENAVN = "NON_EXISTING";
	private static final String PERSON_IDENTER_SOME_OLD_KODENAVN = "SOME_OLD_KODENAVN";
	private static final String PERSON_IDENTER_SOME_FUTURE_KODENAVN = "SOME_FUTURE_KODENAVN";
	private static final String PERSON_IDENTER_SOME_VALID_KODENAVN = "SOME_VALID_KODENAVN";

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
	private static final String KODEVERKV2_FEILKILDE = "KODEVERKSERVER:Kodeverk:hentKodeverk";


	private LocalDateTime tenDaysAgo;
	private LocalDateTime fiveDaysAgo;
	private LocalDateTime fiveDaysIntoTheFuture;
	private LocalDateTime now;

	@Mock
	private KodeverkV2 kodeverkV2Mock;

	@InjectMocks
	private KodeverkConsumer kodeverkConsumer;

	private HentKodeverkRequest requestForPersonIdenter;

	private KodeverkPortType kodeverkPortType;
	@Before
	public void setUp() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet, DatatypeConfigurationException {
		now = LocalDateTime.now();

		tenDaysAgo = LocalDateTime.now().minusDays(10);
		fiveDaysAgo = LocalDateTime.now().minusDays(5);
		fiveDaysIntoTheFuture =  LocalDateTime.now().plusDays(5);

		requestForPersonIdenter = createHentKodeverkRequest(PERSONIDENTER_KODEVERKSNAVN);

		kodeverkPortType = mock(KodeverkPortType.class);
		doReturn(kodeverkPortType).when(kodeverkV2Mock).getKodeverkV2();
		doReturn(createHentKodeverkResponseForPersonIdenter()).
				when(kodeverkPortType).hentKodeverk(anyObject());
	}

	@Test(expected = IllegalArgumentException.class)
	public void hasKodeWhenKodeverksparameterIsNull() throws ExecutionException {
		kodeverkConsumer.hasKode(null, PERSON_IDENTER_FDAT_KODENAVN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void hasKodeWhenKodenavnparameterIsNull() throws ExecutionException {
		kodeverkConsumer.hasKode(PERSONIDENTER_KODEVERKSNAVN, null);
	}

	@Test
	public void hasKodeGivesTrueWhenKodeverkHasKode() throws ExecutionException {
		assertThat(kodeverkConsumer.hasKode(PERSONIDENTER_KODEVERKSNAVN, PERSON_IDENTER_FDAT_KODENAVN), is(true));
	}

	@Test
	public void hasPersonIdenterKodeGivesTrueWhenKodeverkHasKode() throws ExecutionException {
		assertThat(kodeverkConsumer.hasPersonIdenterKode(PERSON_IDENTER_FDAT_KODENAVN), is(true));
	}

	@Test
	public void hasKodeGivesFalseWhenKodeverkDoesNotHasKode() throws ExecutionException {
		assertThat(kodeverkConsumer.hasKode(PERSONIDENTER_KODEVERKSNAVN, PERSON_IDENTER_NON_EXSISTING_KODENAVN), is(false));
	}

	@Test
	public void hasKodeGivesFalseWhenKodeverkIsOld() throws ExecutionException {
		assertThat(kodeverkConsumer.hasKode(PERSONIDENTER_KODEVERKSNAVN, PERSON_IDENTER_SOME_OLD_KODENAVN), is(false));
	}

	@Test
	public void hasKodeGivesFalseWhenKodeverkIsOnlyValidInTheFuture() throws ExecutionException {
		assertThat(kodeverkConsumer.hasKode(PERSONIDENTER_KODEVERKSNAVN, PERSON_IDENTER_SOME_FUTURE_KODENAVN), is(false));
	}

	@Test
	public void hasKodeGivesTrueWhenKodeverkIsWithPeriodeThatEnclosesNow() throws ExecutionException {
		assertThat(kodeverkConsumer.hasKode(PERSONIDENTER_KODEVERKSNAVN, PERSON_IDENTER_SOME_VALID_KODENAVN), is(true));
	}


	@Test(expected = ExecutionException.class)
	public void hasKodeKasterHentKodeverkHentKodeverIkkeFunnet() throws DatatypeConfigurationException, ExecutionException,
			HentKodeverkHentKodeverkKodeverkIkkeFunnet {

		reset(kodeverkPortType);

		HentKodeverkHentKodeverkKodeverkIkkeFunnet ikkeFunnet =
				new HentKodeverkHentKodeverkKodeverkIkkeFunnet(
						"Fant ingen versjoner av kodeverk med navn " + NON_EXISITING_KODEVERKSNAVN,
						feilResponse(NON_EXISITING_KODEVERKSNAVN));

		doThrow(ikkeFunnet).when(kodeverkPortType).hentKodeverk(anyObject());
		kodeverkConsumer.hasKode(NON_EXISITING_KODEVERKSNAVN, PERSON_IDENTER_FDAT_KODENAVN);
	}

	private HentKodeverkResponse createHentKodeverkResponseForPersonIdenter() throws DatatypeConfigurationException {
		HentKodeverkResponse result = new HentKodeverkResponse();
		EnkeltKodeverk kodeverk = new EnkeltKodeverk();
		kodeverk.setNavn(PERSONIDENTER_KODEVERKSNAVN);
		addKode(kodeverk, PERSON_IDENTER_FDAT_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_FNR_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_BOST_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_DNR_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_SOME_OLD_KODENAVN, tenDaysAgo, fiveDaysAgo);
		addKode(kodeverk, PERSON_IDENTER_SOME_FUTURE_KODENAVN, fiveDaysIntoTheFuture, null);
		addKode(kodeverk, PERSON_IDENTER_SOME_VALID_KODENAVN, fiveDaysAgo, fiveDaysIntoTheFuture);
		result.setKodeverk(kodeverk);
		return result;
	}

	private void addKode(EnkeltKodeverk kodeverk, String kodenavn) {
		Kode kode = new Kode();
		kode.setNavn(kodenavn);
		kodeverk.getKode().add(kode);
	}

	private void addKode(EnkeltKodeverk kodeverk, String kodenavn, LocalDateTime fom, LocalDateTime tom) throws DatatypeConfigurationException {
		Kode kode = new Kode();
		kode.setNavn(kodenavn);
		Periode periode = new Periode();
		periode.setFom(fom != null ? toXmlGregorianCalendar(fom) : null);
		periode.setTom(tom != null ? toXmlGregorianCalendar(tom) : null);
		kode.getGyldighetsperiode().add(periode);
		kodeverk.getKode().add(kode);
	}

	private HentKodeverkRequest createHentKodeverkRequest(String kodeverksnavn) {
		HentKodeverkRequest result = new HentKodeverkRequest();
		result.setNavn(kodeverksnavn);
		return result;
	}

	private KodeverkIkkeFunnet feilResponse(String kodeverksnavn) throws DatatypeConfigurationException {
		KodeverkIkkeFunnet result = new KodeverkIkkeFunnet();
		result.setFeilkilde(KODEVERKV2_FEILKILDE);
		result.setFeilaarsak("no.nav.kodeverk.common.exception.NotFoundException: Fant ingen versjoner av kodeverk med navn " + kodeverksnavn);
		result.setFeilmelding("Fant ingen versjoner av kodeverk med navn " + kodeverksnavn);
		result.setTidspunkt(toXmlGregorianCalendar(now));
		return result;
	}
}
