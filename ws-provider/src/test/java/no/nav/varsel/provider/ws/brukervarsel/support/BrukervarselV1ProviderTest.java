package no.nav.varsel.provider.ws.brukervarsel.support;

import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo.Builder.aHentVarselForBrukerResponseTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import no.nav.varsel.provider.map.support.HentVarselForBrukerRequestMapper;
import no.nav.varsel.provider.map.support.HentVarselForBrukerResponseMapper;
import no.nav.varsel.provider.map.support.VarselMapper;
import no.nav.varsel.provider.map.support.VarselbestillingMapper;
import no.nav.varsel.provider.map.support.HentVarselForBrukerRequestValidator;
import no.nav.varsel.service.BrukervarselV1Service;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;


/**
 * Unit test for BrukervarselV1Provider
 *
 * @author Lars Aune
 */
@RunWith(MockitoJUnitRunner.class)
public class BrukervarselV1ProviderTest {
	public static final String AKTOER_ID = "AKTOER_ID";
	public static final String VARSEL_TYPE_ID = "VARSEL_TYPE_ID";
	public static final String KANAL = "KANAL";
	public static final String KONTAKTINFO = "KONTAKT_INFO";
	public static final String VARSELTEKST = "VARSEL_TEKST";
	public static final String VARSEL_URL = "VARSEL_URL";
	public static final String VARSELTITTEL = "VARSELTITTEL";
	private static final LocalDateTime BESTILINGSTIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 1, 0, 0, 0, 0);
	private static final LocalDateTime SISTE_VARSEL_UTSENDELSE = LocalDateTime.of(2016, Month.JULY, 3, 0, 0, 0);
	private static final LocalDateTime SENDT_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 3, 0, 0, 0, 0);
	private static final LocalDateTime DISTRIBUSJON_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 4, 0, 0, 0, 0);
	private static final boolean REVARSEL = true;
	private static final XMLGregorianCalendar FIRST_OF_JUNE_2016 =
			XmlGregorianConverter.toXmlGregorianCalendar(LocalDateTime.of(2016, Month.JUNE, 1, 0, 0, 0, 0));
	private static final XMLGregorianCalendar THIRD_OF_JULY_2016 =
			XmlGregorianConverter.toXmlGregorianCalendar(LocalDateTime.of(2016, Month.JULY, 3, 0, 0, 0, 0));

	private static final XMLGregorianCalendar BESTIL_XML_DATO =
			XmlGregorianConverter.toXmlGregorianCalendar(BESTILINGSTIDSPUNKT);

	private static final XMLGregorianCalendar SISTE_VARSEL_UTSENDELSE_XML_DATO =
			XmlGregorianConverter.toXmlGregorianCalendar(SISTE_VARSEL_UTSENDELSE);

	private static final XMLGregorianCalendar DISTRIBUERT_XML_DATO =
			XmlGregorianConverter.toXmlGregorianCalendar(DISTRIBUSJON_TIDSPUNKT);

	private static final XMLGregorianCalendar SENDT_XML_DATO =
			XmlGregorianConverter.toXmlGregorianCalendar(SENDT_TIDSPUNKT);
	public static final int REVARSLING_INTERVALL = 5;

	@Spy
	private HentVarselForBrukerRequestMapper hentVarselForBrukerRequestMapper;

	@Spy
	private HentVarselForBrukerResponseMapper hentVarselForBrukerResponseMapper;

	@Spy
	private VarselbestillingMapper varselbestillingMapper;

	@Spy
	private VarselMapper varselMapper;

	@Mock
	private BrukervarselV1Service brukervarselV1Service;

	@InjectMocks
	private BrukervarselV1Provider brukervarselV1Provider;
	private AktoerId bruker;


	@Before
	public void onSetup() {
		varselbestillingMapper.setVarselMapper(varselMapper);
		hentVarselForBrukerResponseMapper.setVarselbestillingMapper(varselbestillingMapper);
		bruker = new AktoerId();
		bruker.setAktoerId(AKTOER_ID);
	}

	@Test
	public void shouldPing() {
		brukervarselV1Provider.ping();
	}

	@Test
	public void shouldGiveResponse() throws HentVarselForBrukerUgyldigInput {

		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		request.setBruker(bruker);
		Periode periode = new Periode();
		periode.setFom(FIRST_OF_JUNE_2016);
		periode.setTom(THIRD_OF_JULY_2016);
		request.setPeriode(periode);

		HentVarselForBrukerResponseTo.Builder responseToBuilder = aHentVarselForBrukerResponseTo();
		List<VarselTo> varsler = new ArrayList<>();

		VarselTo.Builder varselToBuilder = new VarselTo.Builder();
		VarselTo varselTo = varselToBuilder.
				kanal(KANAL).
				sendtTidspunkt(SENDT_TIDSPUNKT).
				distribusjonTidspunkt(DISTRIBUSJON_TIDSPUNKT).
				kontaktInfo(KONTAKTINFO).
				varselTittel(VARSELTITTEL).
				varselTekst(VARSELTEKST).
				varselURL(VARSEL_URL).
				revarsel(REVARSEL).
				build();

		varsler.add(varselTo);

		VarselbestillingTo.Builder varselbestillingToBuilder = new VarselbestillingTo.Builder();
		VarselbestillingTo varselbestillingTo = varselbestillingToBuilder.
				varseltypeId(VARSEL_TYPE_ID).
				aktoerId(AKTOER_ID).
				bestillingstidspunkt(BESTILINGSTIDSPUNKT).
				revarslingIntervall(REVARSLING_INTERVALL).
				sisteVarselUtsendelse(SISTE_VARSEL_UTSENDELSE).
				varsler(varsler).
				build();

		List<VarselbestillingTo> brukersVarsler = new ArrayList<>();
		brukersVarsler.add(varselbestillingTo);

		HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo = responseToBuilder.varselbestillingTos(brukersVarsler).build();

		when(brukervarselV1Service.hentVarselForBruker(anyObject())).thenReturn(hentVarselForBrukerResponseTo);

		HentVarselForBrukerResponse response = brukervarselV1Provider.hentVarselForBruker(request);

		List<Varselbestilling> varselbestillingListe = response.getBrukervarsel().getVarselbestillingListe();
		assertThat(varselbestillingListe, hasSize(1));
		Varselbestilling varselbestilling = varselbestillingListe.get(0);
		assertThat(varselbestilling.getAktoerId().getAktoerId(), is(bruker.getAktoerId()));
		assertThat(varselbestilling.getPerson(), nullValue());
		assertThat(varselbestilling.getBestilt(), is(BESTIL_XML_DATO));
		assertThat(varselbestilling.getReVarselingsintervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getSisteVarselutsendelse(), is(SISTE_VARSEL_UTSENDELSE_XML_DATO));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSEL_TYPE_ID));
		assertThat(varselbestilling.getVarselListe(), hasSize(1));
		Varsel varsel =	varselbestilling.getVarselListe().get(0);
		assertThat(varsel.getDistribuert(), is(DISTRIBUERT_XML_DATO));
		assertThat(varsel.getKanal(), is(KANAL));
		assertThat(varsel.getKontaktinfo(),is(KONTAKTINFO));
		assertThat(varsel.getSendt(), is(SENDT_XML_DATO));
		assertThat(varsel.getVarseltekst(), is(VARSELTEKST));
		assertThat(varsel.getVarselURL(), is(VARSEL_URL));
		assertThat(varsel.getVarseltittel(), is(VARSELTITTEL));

		reset(brukervarselV1Service);
	}
}