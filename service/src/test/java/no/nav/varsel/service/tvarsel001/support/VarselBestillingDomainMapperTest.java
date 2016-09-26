package no.nav.varsel.service.tvarsel001.support;

import static java.util.Collections.singletonMap;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_NAVN;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_URL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Unit test for {@link VarselBestillingDomainMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class VarselBestillingDomainMapperTest {

	private static final LocalDateTime UTLOEPSTIDSPUNKT = LocalDateTime.parse("2013-12-03T21:25:45");
	private static final String AKTOER_ID = "aktoerId";
	private static final String ORG_NR = "org_nr";
	private static final String PERSONIDENT = "personident";
	private static final String VARSLIGNSTYPE = "varslignstype";
	private static final String KEY = "key";
	private static final String VALUE = "val";
	private static final String FOERSTEGANGS_TEKST = "foreste tekst for {key}";
	private static final String REVARSLING_TEKST = "revarsling tekst for {key}";
	private static final String TITTEL = "tittel {key}";
	private static final String MOBILTELEFONNUMMER = "12345678";
	private static final String EPOSTADRESSE = "epost@epost.no";
	private static final HashSet<KanalCode> KANALER = Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS);
	private static final String BESTILLING_ID = "b592d5f1-7506-462d-9814-4c2d92bf8946";

	private static final String VARSEL_FOR_DIST_KANAL = "vardistkanal";
	private static final String VARSEL_KATEGORI = "varkat";
	private static final boolean INAKTIV = false;
	private static final int REVARSLING_INTERVALL = 4;
	private static final int ANTALL_REVARSLING = 2;
	private static final KanalCode PREFERERT_KANAL = KanalCode.DITT_NAV;
	private static final String BASE_URL = "baseurl";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Spy
	private VarselFletter varselFletter;
	@InjectMocks
	private VarselBestillingDomainMapper mapper;

	@Before
	public void setUp() throws Exception {
		varselFletter.setVarselUrlFromFasit(BASE_URL);
	}

	@Test
	public void shouldMapToDomainFoerstegangVarselUtenRevarsel() throws Exception {
		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(createBestillTo(), createVarselTo(), createDigitalKontaktinfoTo());

		assertThat(varselbestilling.getVarselbestillingId(), is(BESTILLING_ID));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSLIGNSTYPE));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(UTLOEPSTIDSPUNKT));
		assertThat(varselbestilling.getFnr(), is(PERSONIDENT));
		assertThat(varselbestilling.getAktorId(), is(AKTOER_ID));
		assertThat(varselbestilling.getOrgNr(), is(ORG_NR));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getVarsels(), hasSize(2));

		Varsel smsVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.SMS).findFirst().get();
		Varsel epostVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.EPOST).findFirst().get();
		assertThat(UUID.fromString(smsVarsel.getVarselId()).toString(), is(smsVarsel.getVarselId()));
		assertThat(smsVarsel.getKanal(), is(KanalCode.SMS));
		assertThat(smsVarsel.getKontaktInfo(), is(MOBILTELEFONNUMMER));
		assertThat(smsVarsel.getStatus(), is(StatusCode.SENDT));
		assertThat(smsVarsel.getVarselTittel(), is(TITTEL.replace("{key}", VALUE)));
		assertThat(smsVarsel.getVarselTekst(), is(FOERSTEGANGS_TEKST.replace("{key}", VALUE)));
		assertThat(smsVarsel.getVarselUrl(), nullValue());
		assertThat(smsVarsel.getSendtTidspunkt(), aboutNow());
		assertThat(smsVarsel.getErRevarsel(), is(false));

		assertThat(epostVarsel.getKanal(), is(KanalCode.EPOST));
		assertThat(epostVarsel.getKontaktInfo(), is(EPOSTADRESSE));
		assertThat(epostVarsel.getVarselUrl(), nullValue());
	}

	@Test
	public void shouldMapToDomainFoerstegangVarselMedRevarsel() throws Exception {
		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselMedRevarsel(createBestillTo(), createVarselTo(), createDigitalKontaktinfoTo());

		assertThat(varselbestilling.getVarselbestillingId(), is(BESTILLING_ID));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSLIGNSTYPE));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(UTLOEPSTIDSPUNKT));
		assertThat(varselbestilling.getFnr(), is(PERSONIDENT));
		assertThat(varselbestilling.getAktorId(), is(AKTOER_ID));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getAntallRevarslinger(), is(ANTALL_REVARSLING));
		assertThat(varselbestilling.getNesteVarslingDato(), is(LocalDate.now().plusDays(REVARSLING_INTERVALL)));
		assertThat(varselbestilling.getVarsels(), hasSize(2));

		Varsel smsVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.SMS).findFirst().get();
		Varsel epostVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.EPOST).findFirst().get();
		assertThat(UUID.fromString(smsVarsel.getVarselId()).toString(), is(smsVarsel.getVarselId()));
		assertThat(smsVarsel.getKanal(), is(KanalCode.SMS));
		assertThat(smsVarsel.getKontaktInfo(), is(MOBILTELEFONNUMMER));
		assertThat(smsVarsel.getStatus(), is(StatusCode.SENDT));
		assertThat(smsVarsel.getVarselTittel(), is(TITTEL.replace("{key}", VALUE)));
		assertThat(smsVarsel.getVarselTekst(), is(FOERSTEGANGS_TEKST.replace("{key}", VALUE)));
		assertThat(smsVarsel.getVarselUrl(), nullValue());
		assertThat(smsVarsel.getSendtTidspunkt(), aboutNow());
		assertThat(smsVarsel.getErRevarsel(), is(false));

		assertThat(epostVarsel.getKanal(), is(KanalCode.EPOST));
		assertThat(epostVarsel.getKontaktInfo(), is(EPOSTADRESSE));
		assertThat(epostVarsel.getVarselUrl(), nullValue());
	}

	@Test
	public void shouldMapRevarslingVarsel() throws Exception {
		Varsel varsel = mapper.mapReVarsel(KanalCode.EPOST, createBestillTo(), createVarselTo(), createDigitalKontaktinfoTo());

		assertThat(UUID.fromString(varsel.getVarselId()).toString(), is(varsel.getVarselId()));
		assertThat(varsel.getKanal(), is(KanalCode.EPOST));
		assertThat(varsel.getKontaktInfo(), is(EPOSTADRESSE));
		assertThat(varsel.getStatus(), is(StatusCode.SENDT));
		assertThat(varsel.getVarselTittel(), is(TITTEL.replace("{key}", VALUE)));
		assertThat(varsel.getVarselTekst(), is(REVARSLING_TEKST.replace("{key}", VALUE)));
		assertThat(varsel.getVarselUrl(), nullValue());
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getErRevarsel(), is(true));
	}

	@Test
	public void shouldMapUrlForDittNavButNotKontaktInfo() throws Exception {
		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), createVarselTo(), createDigitalKontaktinfoTo());

		assertThat(varsel.getKanal(), is(KanalCode.DITT_NAV));
		assertThat(varsel.getVarselUrl(), is(VARSEL_URL));
		assertThat(varsel.getKontaktInfo(), nullValue());
	}

	@Test
	public void shouldUseFasitPropertyWhenVarselUrlEquals$navnobaseurl$() throws Exception {
		VarselInfoTo varselTo = createVarselTo();
		varselTo.setVarselUrl("$navnobaseurl$");
		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselTo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is(BASE_URL));
	}

	@Test
	public void shouldWeaveFasitPropertyWhenVarselUrlContains$navnobaseurl$() throws Exception {
		String postfix = "/din-innboks";
		String prefix = "prefix";

		VarselInfoTo varselTo = createVarselTo();
		varselTo.setVarselUrl(prefix + "$navnobaseurl$" + postfix);

		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselTo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), equalTo(prefix + BASE_URL + postfix));
	}

	@Test
	public void shouldUseVarselUrlFromDokkat() throws Exception {
		VarselInfoTo varselTo = createVarselTo();
		varselTo.setVarselUrl("dokkat");
		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselTo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is("dokkat"));
	}

	@Test
	public void shouldHandleNoFlettingFromDokkat() throws Exception {
		VarselInfoTo varselTo = createVarselTo();
		varselTo.setVarselUrl("dokkat");
		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselTo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is("dokkat"));
	}

	@Test
	public void shouldFletteUrl() throws Exception {
		VarselInfoTo varselTo = createVarselTo();
		varselTo.setVarselUrl("dokkat/{id}");

		BestillVarselTo bestillTo = createBestillTo();
		HashMap<String, String> map = Maps.newHashMap();
		map.put("id", "1234");
		map.put(KEY, VALUE);
		bestillTo.setParameters(map);

		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, bestillTo, varselTo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is("dokkat/1234"));
	}

	@Test
	public void shouldNotSetRevarslingsfieldsWhenRevarslingIntervallIsNull() throws Exception {
		VarselInfoTo varselTo = createVarselTo();
		varselTo.setRevarslingIntervall(null);

		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselMedRevarsel(createBestillTo(),
				varselTo,
				createDigitalKontaktinfoTo());

		assertThat(varselbestilling.getNesteVarslingDato(), nullValue());
		assertThat(varselbestilling.getRevarslingIntervall(), nullValue());
		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
	}

	@Test
	public void shouldNotSetRevarslingsfieldsWhenAntallRevarslingIsNull() throws Exception {
		VarselInfoTo varselTo = createVarselTo();
		varselTo.setAntallRevarsling(null);

		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselMedRevarsel(createBestillTo(),
				varselTo,
				createDigitalKontaktinfoTo());

		assertThat(varselbestilling.getNesteVarslingDato(), nullValue());
		assertThat(varselbestilling.getRevarslingIntervall(), nullValue());
		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
	}

	private BestillVarselTo createBestillTo() {
		BestillVarselTo to = new BestillVarselTo();
		to.setVarselBestillingId(BESTILLING_ID);
		to.setPersonIdent(PERSONIDENT);
		to.setAktoerId(AKTOER_ID);
		to.setOrgNr(ORG_NR);
		to.setUtloepstidspunkt(UTLOEPSTIDSPUNKT);
		to.setParameters(singletonMap(KEY, VALUE));
		to.setVarseltypeId(VARSLIGNSTYPE);
		return to;
	}

	private VarselInfoTo createVarselTo() {
		VarselInfoTo to = createVarselInfoTo();

		VarselMalTo malToSms = createVarselMalTo(KanalCode.SMS);
		VarselMalTo malToEpost = createVarselMalTo(KanalCode.EPOST);
		VarselMalTo malToDittnav = createVarselMalTo(KanalCode.DITT_NAV);

		to.setMaler(Sets.newHashSet(malToSms, malToEpost, malToDittnav));
		return to;
	}

	private VarselInfoTo createVarselInfoTo() {
		VarselInfoTo to = new VarselInfoTo();
		to.setVarselForDistKanal(VARSEL_FOR_DIST_KANAL);
		to.setVarselNavn(VARSEL_NAVN);
		to.setVarselKategori(VARSEL_KATEGORI);
		to.setInaktiv(INAKTIV);
		to.setRevarslingIntervall(REVARSLING_INTERVALL);
		to.setAntallRevarsling(ANTALL_REVARSLING);
		to.setVarselUrl(VARSEL_URL);
		to.addPreferertKanal(PREFERERT_KANAL);
		return to;
	}

	private VarselMalTo createVarselMalTo(KanalCode kanalCode) {
		VarselMalTo malToSms = new VarselMalTo();
		malToSms.setTittel(TITTEL);
		malToSms.setKanal(kanalCode);
		malToSms.setFoerstegangsTekst(FOERSTEGANGS_TEKST);
		malToSms.setRevarslingTekst(REVARSLING_TEKST);
		return malToSms;
	}

	private KontaktregisterTo createDigitalKontaktinfoTo() {
		KontaktregisterTo to = new KontaktregisterTo();
		to.setKanaler(KANALER);
		to.setMobiltelefonnummer(MOBILTELEFONNUMMER);
		to.setEpostadresse(EPOSTADRESSE);
		return to;
	}
}