package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSAktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarselbestilling;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerResponse;
import no.nav.varsel.service.BrukervarselV1Service;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static no.nav.varsel.domain.Constants.USER_ID;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo.Builder.aHentVarselForBrukerResponseTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrukervarselV1ProviderTest {

	public static final String AKTOER_ID = "AKTOER_ID";
	public static final String VARSEL_TYPE_ID = "VARSEL_TYPE_ID";
	public static final String KANAL = "KANAL";
	public static final String KONTAKTINFO = "KONTAKT_INFO";
	public static final String VARSELTEKST = "VARSEL_TEKST";
	public static final String VARSEL_URL = "VARSEL_URL";
	public static final String VARSELTITTEL = "VARSELTITTEL";
	public static final int REVARSLING_INTERVALL = 5;
	private static final boolean REVARSEL = true;

	private static final LocalDateTime BESTILINGSTIDSPUNKT = LocalDateTime.of(2016, JULY, 1, 0, 0, 0, 0);
	private static final LocalDateTime SISTE_VARSEL_UTSENDELSE = LocalDateTime.of(2016, JULY, 3, 0, 0, 0);
	private static final LocalDateTime SENDT_TIDSPUNKT = LocalDateTime.of(2016, JULY, 3, 0, 0, 0, 0);
	private static final LocalDateTime DISTRIBUSJON_TIDSPUNKT = LocalDateTime.of(2016, JULY, 4, 0, 0, 0, 0);
	private static final XMLGregorianCalendar FIRST_OF_JUNE_2016 = toXmlGregorianCalendar(LocalDateTime.of(2016, JUNE, 1, 0, 0, 0, 0));
	private static final XMLGregorianCalendar THIRD_OF_JULY_2016 = toXmlGregorianCalendar(LocalDateTime.of(2016, JULY, 3, 0, 0, 0, 0));
	private static final XMLGregorianCalendar BESTIL_XML_DATO = toXmlGregorianCalendar(BESTILINGSTIDSPUNKT);
	private static final XMLGregorianCalendar SISTE_VARSEL_UTSENDELSE_XML_DATO = toXmlGregorianCalendar(SISTE_VARSEL_UTSENDELSE);
	private static final XMLGregorianCalendar DISTRIBUERT_XML_DATO = toXmlGregorianCalendar(DISTRIBUSJON_TIDSPUNKT);
	private static final XMLGregorianCalendar SENDT_XML_DATO = toXmlGregorianCalendar(SENDT_TIDSPUNKT);

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
	private WSAktoerId bruker;

	@BeforeEach
	public void onSetup() {
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
		System.setProperty("no.nav.modig.security.systemuser.username", "varsel");
		System.setProperty("no.nav.modig.security.systemuser.password", "passord");
		SubjectHandlerUtils.setInternBruker(USER_ID);

		varselbestillingMapper.setVarselMapper(varselMapper);
		hentVarselForBrukerResponseMapper.setVarselbestillingMapper(varselbestillingMapper);
		bruker = new WSAktoerId();
		bruker.setAktoerId(AKTOER_ID);
	}

	@Test
	public void shouldPing() {
		brukervarselV1Provider.ping();
	}

	@Test
	public void shouldGiveResponse() {

		WSHentVarselForBrukerRequest request = new WSHentVarselForBrukerRequest();
		request.setBruker(bruker);
		WSPeriode periode = new WSPeriode();
		periode.setFom(FIRST_OF_JUNE_2016);
		periode.setTom(THIRD_OF_JULY_2016);
		request.setPeriode(periode);

		HentVarselForBrukerResponseTo.Builder responseToBuilder = aHentVarselForBrukerResponseTo();
		List<VarselTo> varsler = new ArrayList<>();

		VarselTo varselTo = new VarselTo.Builder()
				.kanal(KANAL)
				.sendtTidspunkt(SENDT_TIDSPUNKT)
				.distribusjonTidspunkt(DISTRIBUSJON_TIDSPUNKT)
				.kontaktInfo(KONTAKTINFO)
				.varselTittel(VARSELTITTEL)
				.varselTekst(VARSELTEKST)
				.varselURL(VARSEL_URL)
				.revarsel(REVARSEL)
				.build();

		varsler.add(varselTo);

		VarselbestillingTo varselbestillingTo = new VarselbestillingTo.Builder()
				.varseltypeId(VARSEL_TYPE_ID)
				.aktoerId(AKTOER_ID)
				.bestillingstidspunkt(BESTILINGSTIDSPUNKT)
				.revarslingIntervall(REVARSLING_INTERVALL)
				.sisteVarselUtsendelse(SISTE_VARSEL_UTSENDELSE)
				.varsler(varsler)
				.build();

		List<VarselbestillingTo> brukersVarsler = new ArrayList<>();
		brukersVarsler.add(varselbestillingTo);

		HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo = responseToBuilder.varselbestillingTos(brukersVarsler).build();

		when(brukervarselV1Service.hentVarselForBruker(any())).thenReturn(hentVarselForBrukerResponseTo);

		WSHentVarselForBrukerResponse response = brukervarselV1Provider.hentVarselForBruker(request);

		List<WSVarselbestilling> varselbestillingListe = response.getBrukervarsel().getVarselbestillingListe();
		assertThat(varselbestillingListe, hasSize(1));
		WSVarselbestilling varselbestilling = varselbestillingListe.get(0);
		assertThat(varselbestilling.getAktoerId().getAktoerId(), is(bruker.getAktoerId()));
		assertThat(varselbestilling.getPerson(), nullValue());
		assertThat(varselbestilling.getBestilt(), is(BESTIL_XML_DATO));
		assertThat(varselbestilling.getReVarselingsintervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getSisteVarselutsendelse(), is(SISTE_VARSEL_UTSENDELSE_XML_DATO));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSEL_TYPE_ID));
		assertThat(varselbestilling.getVarselListe(), hasSize(1));
		WSVarsel varsel = varselbestilling.getVarselListe().get(0);
		assertThat(varsel.getDistribuert(), is(DISTRIBUERT_XML_DATO));
		assertThat(varsel.getKanal(), is(KANAL));
		assertThat(varsel.getKontaktinfo(), is(KONTAKTINFO));
		assertThat(varsel.getSendt(), is(SENDT_XML_DATO));
		assertThat(varsel.getVarseltekst(), is(VARSELTEKST));
		assertThat(varsel.getVarselURL(), is(VARSEL_URL));
		assertThat(varsel.getVarseltittel(), is(VARSELTITTEL));

		reset(brukervarselV1Service);
	}
}