package no.nav.varsel.service.tvarsel001.support;

import static java.util.Collections.singletonMap;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.tvarsel001.to.BestillServicemeldingTo;
import no.nav.varsel.wsconsumer.dkif.to.DigitalKontaktinfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

/**
 * Unit test for {@link ServicemeldingDomainMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServicemeldingDomainMapperTest {

	public static final LocalDateTime UTLOEPSTIDSPUNKT = LocalDateTime.parse("2013-12-03T21:25:45");
	public static final String AKTOER_ID = "aktoerId";
	public static final String PERSONIDENT = "personident";
	public static final String VARSLIGNSTYPE = "varslignstype";
	public static final String KEY = "key";
	public static final String VALUE = "val";
	public static final String URL = "url";
	public static final String PREFERERT_KANAL = "DITTNAV";
	public static final String FOERSTEGANGS_TEKST = "foreste tekst for :key";
	public static final String REVARSLING_TEKST = "revarsling tekst for :key";
	public static final String TITTEL = "tittel";
	public static final String MOBILTELEFONNUMMER = "12345678";
	public static final String EPOSTADRESSE = "epost@epost.no";
	public static final HashSet<KanalCode> KANALER = Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS);

	@Spy
	private VarselFletter varselFletter;
	@InjectMocks
	private ServicemeldingDomainMapper mapper;

	@Test
	public void mapToDomain() throws Exception {
		Varselbestilling varselbestilling = mapper.mapToDomain(createBestillTo(), createVarselTo(), createDigitalKontaktinfoTo());

		assertThat(UUID.fromString(varselbestilling.getVarselbestillingId()).toString(), is(varselbestilling.getVarselbestillingId()));
		assertThat(varselbestilling.getVarslingstype(), is(VARSLIGNSTYPE));
		assertThat(varselbestilling.getPreferertKanal(), is(PREFERERT_KANAL));
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
		assertThat(smsVarsel.getStatus(), is(StatusCode.OPPRETTET));
		assertThat(smsVarsel.getVarselTittel(), is(TITTEL));
		assertThat(smsVarsel.getVarselTekst(), is(FOERSTEGANGS_TEKST.replace(":" + KEY, VALUE)));
		assertThat(smsVarsel.getVarselUrl(), is(URL));

		assertThat(epostVarsel.getKanal(), is(KanalCode.EPOST));
		assertThat(epostVarsel.getKontaktInfo(), is(EPOSTADRESSE));
	}

	@Test
	public void decideKanaler() throws Exception {

	}

	private BestillServicemeldingTo createBestillTo() {
		BestillServicemeldingTo to = new BestillServicemeldingTo();
		to.setPersonIdent(PERSONIDENT);
		to.setAktoerId(AKTOER_ID);
		to.setUtloepstidspunkt(UTLOEPSTIDSPUNKT);
		to.setParameters(singletonMap(KEY, VALUE));
		to.setVarslingstype(VARSLIGNSTYPE);
		return to;
	}

	private VarselInfoTo createVarselTo() {
		VarselInfoTo to = new VarselInfoTo();
		to.setVarselURL(URL);
		to.setPreferertKanal(PREFERERT_KANAL);

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

		to.setMaler(Sets.newHashSet(malToSms, malToEpost));
		return to;
	}

	private DigitalKontaktinfoTo createDigitalKontaktinfoTo() {
		DigitalKontaktinfoTo to = new DigitalKontaktinfoTo();
		to.setKanaler(KANALER);
		to.setMobiltelefonnummer(MOBILTELEFONNUMMER);
		to.setEpostadresse(EPOSTADRESSE);
		return to;
	}

}