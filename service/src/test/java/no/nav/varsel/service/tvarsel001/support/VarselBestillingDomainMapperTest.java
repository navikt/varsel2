package no.nav.varsel.service.tvarsel001.support;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.to.Varselinfo;
import no.nav.varsel.consumer.dokmet.to.Varselmal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.support.exception.functional.VarselTekstMissingException;
import no.nav.varsel.service.to.BestillVarselTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.singletonMap;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
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

	private static final String VARSEL_NAVN = "varselnavn";
	private static final String VARSEL_URL = "http://nav.no";
	private static final String VARSEL_FOR_DIST_KANAL = "vardistkanal";
	private static final String VARSEL_KATEGORI = "varkat";
	private static final boolean INAKTIV = false;
	private static final int REVARSLING_INTERVALL = 4;
	private static final int ANTALL_REVARSLING = 2;
	private static final KanalCode PREFERERT_KANAL = KanalCode.DITT_NAV;
	private static final String BASE_URL = "baseurl";

	@Spy
	private VarselFletter varselFletter;
	@InjectMocks
	private VarselBestillingDomainMapper mapper;

	@BeforeEach
	public void setUp() {
		varselFletter.setVarselUrlFromFasit(BASE_URL);
	}

	@Test
	public void shouldMapToDomainFoerstegangVarselUtenRevarsel() {
		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(createBestillTo(), createVarselinfo(), createDigitalKontaktinfoTo());

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
	public void shouldMapToDomainFoerstegangVarselMedRevarsel() {
		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselMedRevarsel(createBestillTo(), createVarselinfo(), createDigitalKontaktinfoTo());

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
	public void shouldThrowWhenFoerstegangVarselMissingVarselTekst() {
		String expectedErrMsg = String.format("Missing varseltekst for varselbestillingsId=%s, kanalCode=%s", BESTILLING_ID, KanalCode.EPOST);
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.getMal(KanalCode.EPOST).setFoerstegangsTekst(null);

		Executable executable = () -> mapper.mapVarselbestillingFoerstegangVarselMedRevarsel(createBestillTo(), varselinfo, createDigitalKontaktinfoTo());

		Exception exception = Assertions.assertThrows(VarselTekstMissingException.class, executable);
		assertEquals(exception.getMessage(), expectedErrMsg);

	}

	@Test
	public void shouldMapRevarslingVarsel() {
		Varsel varsel = mapper.mapReVarsel(KanalCode.EPOST, createBestillTo(), createVarselinfo(), createDigitalKontaktinfoTo());

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
	public void shouldMapUrlForDittNavButNotKontaktInfo() {
		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), createVarselinfo(), createDigitalKontaktinfoTo());

		assertThat(varsel.getKanal(), is(KanalCode.DITT_NAV));
		assertThat(varsel.getVarselUrl(), is(VARSEL_URL));
		assertThat(varsel.getKontaktInfo(), nullValue());
	}

	@Test
	public void shouldUseFasitPropertyWhenVarselUrlEquals$navnobaseurl$() {
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.setVarselUrl("$navnobaseurl$");

		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselinfo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is(BASE_URL));
	}

	@Test
	public void shouldWeaveFasitPropertyWhenVarselUrlContains$navnobaseurl$() {
		String postfix = "/din-innboks";
		String prefix = "prefix";

		Varselinfo varselinfo = createVarselinfo();
		varselinfo.setVarselUrl(prefix + "$navnobaseurl$" + postfix);

		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselinfo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), equalTo(prefix + BASE_URL + postfix));
	}

	@Test
	public void shouldUseVarselUrlFromDokkat() {
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.setVarselUrl("dokkat");

		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselinfo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is("dokkat"));
	}

	@Test
	public void shouldHandleNoFlettingFromDokkat() {
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.setVarselUrl("dokkat");

		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselinfo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is("dokkat"));
	}

	@Test
	public void shouldFletteUrl() {
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.setVarselUrl("dokkat/{id}");

		BestillVarselTo bestillTo = createBestillTo();
		HashMap<String, String> map = Maps.newHashMap();
		map.put("id", "1234");
		map.put(KEY, VALUE);
		bestillTo.setParameters(map);

		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, bestillTo, varselinfo, createDigitalKontaktinfoTo());

		assertThat(varsel.getVarselUrl(), is("dokkat/1234"));
	}

	@Test
	public void shouldStopRevarslingWhenMissingRevarslingstekst() {
		String expectedErrMsg = String.format("Missing varseltekst for varselbestillingsId=%s, kanalCode=%s", BESTILLING_ID, KanalCode.DITT_NAV);
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.getMal(KanalCode.DITT_NAV).setRevarslingTekst(null);

		Executable executable = () -> mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), varselinfo, createDigitalKontaktinfoTo());

		Exception exception = Assertions.assertThrows(VarselTekstMissingException.class, executable);
		assertEquals(exception.getMessage(), expectedErrMsg);
	}

	@Test
	public void shouldNotSetRevarslingsfieldsWhenRevarslingIntervallIsNull() {
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.setRevarslingIntervall(null);

		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselMedRevarsel(createBestillTo(),
				varselinfo,
				createDigitalKontaktinfoTo());

		assertThat(varselbestilling.getNesteVarslingDato(), nullValue());
		assertThat(varselbestilling.getRevarslingIntervall(), nullValue());
		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
	}

	@Test
	public void shouldNotSetRevarslingsfieldsWhenAntallRevarslingIsNull() {
		Varselinfo varselinfo = createVarselinfo();
		varselinfo.setAntallRevarsling(null);

		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselMedRevarsel(createBestillTo(),
				varselinfo,
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

	private Varselinfo createVarselinfo() {
		Varselinfo varselinfo = createVarselinfoWithoutMaler();

		Varselmal malToSms = createVarselmal(KanalCode.SMS);
		Varselmal malToEpost = createVarselmal(KanalCode.EPOST);
		Varselmal malToDittnav = createVarselmal(KanalCode.DITT_NAV);
		varselinfo.setMaler(Sets.newHashSet(malToSms, malToEpost, malToDittnav));

		return varselinfo;
	}

	private Varselinfo createVarselinfoWithoutMaler() {
		return Varselinfo.builder()
				.varselForDistKanal(VARSEL_FOR_DIST_KANAL)
				.varselNavn(VARSEL_NAVN)
				.varselKategori(VARSEL_KATEGORI)
				.inaktiv(INAKTIV)
				.revarslingIntervall(REVARSLING_INTERVALL)
				.antallRevarsling(ANTALL_REVARSLING)
				.varselUrl(VARSEL_URL)
				.preferertKanal(Set.of(PREFERERT_KANAL))
				.build();
	}

	private Varselmal createVarselmal(KanalCode kanalCode) {
		return Varselmal.builder()
				.tittel(TITTEL)
				.kanal(kanalCode)
				.foerstegangsTekst(FOERSTEGANGS_TEKST)
				.revarslingTekst(REVARSLING_TEKST)
				.build();
	}

	private KontaktregisterTo createDigitalKontaktinfoTo() {
		KontaktregisterTo to = new KontaktregisterTo();
		to.setKanaler(KANALER);
		to.setMobiltelefonnummer(MOBILTELEFONNUMMER);
		to.setEpostadresse(EPOSTADRESSE);
		return to;
	}
}