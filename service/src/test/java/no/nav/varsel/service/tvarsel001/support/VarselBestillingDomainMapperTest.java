package no.nav.varsel.service.tvarsel001.support;

import static java.util.Collections.singletonMap;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

/**
 * Unit test for {@link VarselBestillingDomainMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class VarselBestillingDomainMapperTest {

	public static final LocalDateTime UTLOEPSTIDSPUNKT = LocalDateTime.parse("2013-12-03T21:25:45");
	public static final String AKTOER_ID = "aktoerId";
	public static final String PERSONIDENT = "personident";
	public static final String VARSLIGNSTYPE = "varslignstype";
	public static final String KEY = "key";
	public static final String VALUE = "val";
	public static final String URL = "url";
	public static final String FOERSTEGANGS_TEKST = "foreste tekst for {key}";
	public static final String REVARSLING_TEKST = "revarsling tekst for {key}";
	public static final String TITTEL = "tittel";
	public static final String MOBILTELEFONNUMMER = "12345678";
	public static final String EPOSTADRESSE = "epost@epost.no";
	public static final HashSet<KanalCode> KANALER = Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS);
	public static final String BESTILLING_ID = "b592d5f1-7506-462d-9814-4c2d92bf8946";


	public static final String VARSEL_FOR_DISTR_KANAL = "vardistkanal";
	public static final String VARSEL_KATEGORI = "varkat";
	public static final boolean INAKTIV = false;
	public static final int REVARSLING_INTERVALL = 4;
	public static final int ANTALL_REVARSLING = 2;
	public static final KanalCode PREFERERT_KANAL = KanalCode.DITT_NAV;

	@Spy
	private VarselFletter varselFletter;
	@InjectMocks
	private VarselBestillingDomainMapper mapper;

	@Test
	public void shouldMapToDomainFoerstegangVarselUtenRevarsel() throws Exception {
		Varselbestilling varselbestilling = mapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(createBestillTo(), createVarselTo(), createDigitalKontaktinfoTo());

		assertThat(varselbestilling.getVarselbestillingId(), is(BESTILLING_ID));
		assertThat(varselbestilling.getVarslingstype(), is(VARSLIGNSTYPE));
		assertThat(varselbestilling.getPreferertKanal(), contains(PREFERERT_KANAL));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(UTLOEPSTIDSPUNKT));
		assertThat(varselbestilling.getFnr(), is(PERSONIDENT));
		assertThat(varselbestilling.getAktorId(), is(AKTOER_ID));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getVarsels(), hasSize(2));

		Varsel smsVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.SMS).findFirst().get();
		Varsel epostVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.EPOST).findFirst().get();
		assertThat(UUID.fromString(smsVarsel.getVarselId()).toString(), is(smsVarsel.getVarselId()));
		assertThat(smsVarsel.getKanal(), is(KanalCode.SMS));
		assertThat(smsVarsel.getKontaktInfo(), is(MOBILTELEFONNUMMER));
		assertThat(smsVarsel.getStatus(), is(StatusCode.SENDT));
		assertThat(smsVarsel.getVarselTittel(), is(TITTEL));
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
		assertThat(varselbestilling.getVarslingstype(), is(VARSLIGNSTYPE));
		assertThat(varselbestilling.getPreferertKanal(), contains(PREFERERT_KANAL));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(UTLOEPSTIDSPUNKT));
		assertThat(varselbestilling.getFnr(), is(PERSONIDENT));
		assertThat(varselbestilling.getAktorId(), is(AKTOER_ID));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getAntallRevarslinger(), is(ANTALL_REVARSLING));
		assertThat(varselbestilling.getNesteVarslingstidspunkt(), is(LocalDate.now().plusDays(REVARSLING_INTERVALL)));
		assertThat(varselbestilling.getVarsels(), hasSize(2));

		Varsel smsVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.SMS).findFirst().get();
		Varsel epostVarsel = varselbestilling.getVarsels().stream().filter(v -> v.getKanal() == KanalCode.EPOST).findFirst().get();
		assertThat(UUID.fromString(smsVarsel.getVarselId()).toString(), is(smsVarsel.getVarselId()));
		assertThat(smsVarsel.getKanal(), is(KanalCode.SMS));
		assertThat(smsVarsel.getKontaktInfo(), is(MOBILTELEFONNUMMER));
		assertThat(smsVarsel.getStatus(), is(StatusCode.SENDT));
		assertThat(smsVarsel.getVarselTittel(), is(TITTEL));
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
		assertThat(varsel.getVarselTittel(), is(TITTEL));
		assertThat(varsel.getVarselTekst(), is(REVARSLING_TEKST.replace("{key}", VALUE)));
		assertThat(varsel.getVarselUrl(), nullValue());
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getErRevarsel(), is(true));
	}

	@Test
	public void shouldMapUrlForDittNavButNotKontaktInfo() throws Exception {
		Varsel varsel = mapper.mapReVarsel(KanalCode.DITT_NAV, createBestillTo(), createVarselTo(), createDigitalKontaktinfoTo());

		assertThat(varsel.getKanal(), is(KanalCode.DITT_NAV));
		assertThat(varsel.getVarselUrl(), is(URL));
		assertThat(varsel.getKontaktInfo(), nullValue());
	}

	private BestillVarselTo createBestillTo() {
		BestillVarselTo to = new BestillVarselTo();
		to.setVarselBestillingId(BESTILLING_ID);
		to.setPersonIdent(PERSONIDENT);
		to.setAktoerId(AKTOER_ID);
		to.setUtloepstidspunkt(UTLOEPSTIDSPUNKT);
		to.setParameters(singletonMap(KEY, VALUE));
		to.setVarslingstype(VARSLIGNSTYPE);
		return to;
	}

	private VarselInfoTo createVarselTo() {
		VarselInfoTo to = new VarselInfoTo();
		to.setVarselForDistrKanal(VARSEL_FOR_DISTR_KANAL);
		to.setVarselKategori(VARSEL_KATEGORI);
		to.setInaktiv(INAKTIV);
		to.setRevarslingIntervall(REVARSLING_INTERVALL);
		to.setAntallRevarsling(ANTALL_REVARSLING);
		to.addPreferertKanal(PREFERERT_KANAL);
		to.setVarselURL(URL);

		VarselMalTo malToSms = new VarselMalTo();
		to.setMaler(Sets.newHashSet(malToSms));
		malToSms.setTittel(TITTEL);
		malToSms.setKanal(KanalCode.SMS);
		malToSms.setFoerstegangsTekst(FOERSTEGANGS_TEKST);
		malToSms.setRevarslingTekst(REVARSLING_TEKST);

		VarselMalTo malToEpost = new VarselMalTo();
		malToEpost.setTittel(TITTEL);
		malToEpost.setKanal(KanalCode.EPOST);
		malToEpost.setFoerstegangsTekst(FOERSTEGANGS_TEKST);
		malToEpost.setRevarslingTekst(REVARSLING_TEKST);

		VarselMalTo malToDittnav = new VarselMalTo();
		malToDittnav.setTittel(TITTEL);
		malToDittnav.setKanal(KanalCode.DITT_NAV);
		malToDittnav.setFoerstegangsTekst(FOERSTEGANGS_TEKST);
		malToDittnav.setRevarslingTekst(REVARSLING_TEKST);

		to.setMaler(Sets.newHashSet(malToSms, malToEpost, malToDittnav));
		return to;
	}

	private KontaktregisterTo createDigitalKontaktinfoTo() {
		KontaktregisterTo to = new KontaktregisterTo();
		to.setKanaler(KANALER);
		to.setMobiltelefonnummer(MOBILTELEFONNUMMER);
		to.setEpostadresse(EPOSTADRESSE);
		return to;
	}

}