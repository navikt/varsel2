package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.Varselinfo;
import no.nav.varsel.consumer.dokmet.Varselmal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.exception.functional.VarselTekstMissingException;
import no.nav.varsel.tvarsel001.service.service.VarselFletter;
import no.nav.varsel.tvarsel001.service.service.to.BestillVarselTo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonMap;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.domain.code.StatusCode.SENDT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;

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
	private static final Set<KanalCode> KANALER = Set.of(EPOST, SMS);
	private static final String BESTILLING_ID = "b592d5f1-7506-462d-9814-4c2d92bf8946";

	private static final String VARSEL_NAVN = "varselnavn";
	private static final String VARSEL_URL = "http://nav.no";
	private static final String VARSEL_FOR_DIST_KANAL = "vardistkanal";
	private static final String VARSEL_KATEGORI = "varkat";
	private static final boolean INAKTIV = false;
	private static final int REVARSLING_INTERVALL = 4;
	private static final int ANTALL_REVARSLING = 2;
	private static final KanalCode PREFERERT_KANAL = DITT_NAV;
	private static final String BASE_URL = "baseurl";
	private static final Set<KanalCode> DITT_NAV_KONTAKTINFO = Set.of(DITT_NAV);

	private final VarselBestillingDomainMapper mapper;

	public VarselBestillingDomainMapperTest() {
		VarselProperties varselProperties = new VarselProperties();
		varselProperties.setUrl(BASE_URL);
		VarselFletter varselFletter = new VarselFletter(varselProperties);
		this.mapper = new VarselBestillingDomainMapper(varselFletter);
	}

	@Test
	public void shouldMapToDomainFoerstegangVarselUtenRevarsel() {
		Varselbestilling varselbestilling = mapper.mapVarselbestilling(createBestillTo(), createVarselinfo(), createDigitalKontaktinfoTo(KANALER));

		assertThat(varselbestilling.getVarselbestillingId()).isEqualTo(BESTILLING_ID);
		assertThat(varselbestilling.getVarseltypeId()).isEqualTo(VARSLIGNSTYPE);
		assertThat(varselbestilling.getUtlopTidspunkt()).isEqualTo(UTLOEPSTIDSPUNKT);
		assertThat(varselbestilling.getFnr()).isEqualTo(PERSONIDENT);
		assertThat(varselbestilling.getAktorId()).isEqualTo(AKTOER_ID);
		assertThat(varselbestilling.getOrgNr()).isEqualTo(ORG_NR);
		assertThat(varselbestilling.getBestillingTidspunkt()).isCloseTo(now(), within(1, SECONDS));

		assertThat(varselbestilling.getVarsels()).hasSize(2);

		Varsel smsVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == SMS).findFirst().get();
		assertThat(smsVarsel.getKanal()).isEqualTo(SMS);
		assertThat(smsVarsel.getKontaktInfo()).isEqualTo(MOBILTELEFONNUMMER);
		assertThat(smsVarsel.getStatus()).isEqualTo(SENDT);
		assertThat(smsVarsel.getVarselTittel()).isEqualTo(TITTEL.replace("{key}", VALUE));
		assertThat(smsVarsel.getVarselTekst()).isEqualTo(FOERSTEGANGS_TEKST.replace("{key}", VALUE));
		assertThat(smsVarsel.getVarselUrl()).isNull();
		assertThat(smsVarsel.getErRevarsel()).isFalse();
		assertThat(smsVarsel.getSendtTidspunkt()).isCloseTo(now(), within(1, SECONDS));

		Varsel epostVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == EPOST).findFirst().get();
		assertThat(epostVarsel.getKanal()).isEqualTo(EPOST);
		assertThat(epostVarsel.getKontaktInfo()).isEqualTo(EPOSTADRESSE);
		assertThat(epostVarsel.getVarselUrl()).isNull();
	}

	@Test
	public void shouldThrowWhenFoerstegangVarselMissingFoerstegangstekst() {
		var varselmalUtenFoerstegangstekst = new Varselmal(EPOST, TITTEL, null, REVARSLING_TEKST);
		var varselinfo = createVarselinfoWithMaler(Set.of(varselmalUtenFoerstegangstekst, createVarselmal(SMS), createVarselmal(DITT_NAV)));

		assertThatExceptionOfType(VarselTekstMissingException.class)
				.isThrownBy(() -> mapper.mapVarselbestilling(createBestillTo(), varselinfo, createDigitalKontaktinfoTo(KANALER)))
				.withMessageContaining(format("Missing varseltekst for varselbestillingsId=%s, kanalCode=%s", BESTILLING_ID, EPOST));
	}


	@Test
	public void shouldMapUrlForDittNavButNotKontaktInfo() {
		Varselbestilling varselbestilling = mapper.mapVarselbestilling(createBestillTo(), createVarselinfo(), createDigitalKontaktinfoTo(DITT_NAV_KONTAKTINFO));

		assertThat(varselbestilling.getVarsels())
				.singleElement()
				.satisfies(varsel -> {
					assertThat(varsel.getKanal()).isEqualTo(DITT_NAV);
					assertThat(varsel.getKontaktInfo()).isNull();
					assertThat(varsel.getVarselUrl()).isEqualTo(VARSEL_URL);
				});
	}

	@Test
	public void shouldUseFasitPropertyWhenVarselUrlEquals$navnobaseurl$() {
		var varselinfo = createVarselinfoWithVarselUrl("$navnobaseurl$");

		Varselbestilling varselbestilling = mapper.mapVarselbestilling(createBestillTo(), varselinfo, createDigitalKontaktinfoTo(DITT_NAV_KONTAKTINFO));

		assertThat(varselbestilling.getVarsels())
				.singleElement()
				.satisfies(varsel -> assertThat(varsel.getVarselUrl()).isEqualTo(BASE_URL));
	}

	@Test
	public void shouldWeaveFasitPropertyWhenVarselUrlContains$navnobaseurl$() {
		String postfix = "/din-innboks";
		String prefix = "prefix";
		var varselinfo = createVarselinfoWithVarselUrl(prefix + "$navnobaseurl$" + postfix);

		Varselbestilling varselbestilling = mapper.mapVarselbestilling(createBestillTo(), varselinfo, createDigitalKontaktinfoTo(DITT_NAV_KONTAKTINFO));

		assertThat(varselbestilling.getVarsels())
				.singleElement()
				.satisfies(varsel -> assertThat(varsel.getVarselUrl()).isEqualTo(prefix + BASE_URL + postfix));
	}

	@Test
	public void shouldFletteUrl() {
		Varselinfo varselinfo = createVarselinfoWithVarselUrl("dokkat/{id}");

		BestillVarselTo bestillTo = createBestillTo();
		HashMap<String, String> map = new HashMap<>();
		map.put("id", "1234");
		map.put(KEY, VALUE);
		bestillTo.setParameters(map);

		Varselbestilling varselbestilling = mapper.mapVarselbestilling(bestillTo, varselinfo, createDigitalKontaktinfoTo(DITT_NAV_KONTAKTINFO));

		assertThat(varselbestilling.getVarsels())
				.singleElement()
				.satisfies(varsel -> assertThat(varsel.getVarselUrl()).isEqualTo("dokkat/1234"));
	}

	@Test
	public void shouldHandleNoFlettingFromDokkat() {
		Varselinfo varselinfo = createVarselinfoWithVarselUrl("dokkat");

		Varselbestilling varselbestilling = mapper.mapVarselbestilling(createBestillTo(), varselinfo, createDigitalKontaktinfoTo(DITT_NAV_KONTAKTINFO));

		assertThat(varselbestilling.getVarsels())
				.singleElement()
				.satisfies(varsel -> assertThat(varsel.getVarselUrl()).isEqualTo("dokkat"));
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
		Varselmal malToSms = createVarselmal(SMS);
		Varselmal malToEpost = createVarselmal(EPOST);
		Varselmal malToDittnav = createVarselmal(DITT_NAV);

		return createVarselinfoWithMaler(Set.of(malToSms, malToEpost, malToDittnav));
	}

	private Varselinfo createVarselinfoWithVarselUrl(String varselUrl) {
		return Varselinfo.builder()
				.varselForDistKanal(VARSEL_FOR_DIST_KANAL)
				.varselNavn(VARSEL_NAVN)
				.varselKategori(VARSEL_KATEGORI)
				.inaktiv(INAKTIV)
				.revarslingIntervall(REVARSLING_INTERVALL)
				.antallRevarsling(ANTALL_REVARSLING)
				.varselUrl(varselUrl)
				.preferertKanal(Set.of(PREFERERT_KANAL))
				.maler(createVarselmaler())
				.build();
	}

	private Set<Varselmal> createVarselmaler() {
		Varselmal malSms = createVarselmal(SMS);
		Varselmal malEpost = createVarselmal(EPOST);
		Varselmal malDittnav = createVarselmal(DITT_NAV);

		return Set.of(malSms, malEpost, malDittnav);
	}

	private Varselinfo createVarselinfoWithMaler(Set<Varselmal> varselmaler) {
		return Varselinfo.builder()
				.varselForDistKanal(VARSEL_FOR_DIST_KANAL)
				.varselNavn(VARSEL_NAVN)
				.varselKategori(VARSEL_KATEGORI)
				.inaktiv(INAKTIV)
				.revarslingIntervall(REVARSLING_INTERVALL)
				.antallRevarsling(ANTALL_REVARSLING)
				.varselUrl(VARSEL_URL)
				.preferertKanal(Set.of(PREFERERT_KANAL))
				.maler(varselmaler)
				.build();
	}

	private Varselmal createVarselmal(KanalCode kanalCode) {
		return new Varselmal(kanalCode, TITTEL, FOERSTEGANGS_TEKST, REVARSLING_TEKST);
	}

	private KontaktregisterTo createDigitalKontaktinfoTo(Set<KanalCode> kanaler) {
		KontaktregisterTo to = new KontaktregisterTo();
		to.setKanaler(kanaler);
		to.setMobiltelefonnummer(MOBILTELEFONNUMMER);
		to.setEpostadresse(EPOSTADRESSE);
		return to;
	}

}