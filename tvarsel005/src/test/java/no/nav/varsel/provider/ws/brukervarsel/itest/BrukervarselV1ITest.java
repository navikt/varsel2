package no.nav.varsel.provider.ws.brukervarsel.itest;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.AbstractWsProviderITest;
import no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static no.nav.varsel.domain.code.StatusCode.FEILET;
import static no.nav.varsel.domain.code.StatusCode.OPPRETTET;
import static no.nav.varsel.domain.code.StatusCode.SENDT;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.BESTILLING_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.DISTRIBUSJON_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.ER_REVARSEL;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.KANAL_CODE;
import static no.nav.varsel.repo.TestdataUtil.KONTAKT_INFO;
import static no.nav.varsel.repo.TestdataUtil.REVARSLING_INTERVALL;
import static no.nav.varsel.repo.TestdataUtil.SENDT_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TEKST;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TITTEL;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_URL;
import static no.nav.varsel.repo.TestdataUtil.createVarselBuilder;
import static no.nav.varsel.repo.TestdataUtil.createVarselUnique;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class BrukervarselV1ITest extends AbstractWsProviderITest {

	private static final LocalDateTime FOM = BESTILLING_TIDSPUNKT.minusHours(1);
	private static final LocalDateTime TOM = BESTILLING_TIDSPUNKT.plusHours(1);

	@Autowired
	private BrukervarselV1Endpoint brukervarselV1;

	@BeforeEach
	public void setUp() {
		varselbestillingRepo.save(createVarselbestilling());
	}

	@Test
	public void shouldPing() {
		brukervarselV1.ping();
	}

	@Test
	public void shouldGetAllBrukervarselForBrukerFnr() throws Exception {
		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest(FNR, null, FOM, TOM));

		assertResponse(response);
	}

	@Test
	public void shouldGetAllBrukervarselForBrukerAktoerId() throws Exception {
		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest(null, AKTOR_ID, FOM, TOM));

		assertResponse(response);
	}

	@Test
	public void shouldNotGetForOtherUser() throws Exception {
		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest("other", null, FOM, TOM));

		assertThat(response.getBrukervarsel(), notNullValue());
		assertThat(response.getBrukervarsel().getVarselbestillingListe(), hasSize(0));
	}

	@Test
	public void shouldGetAllBrukervarselForBrukerFOMIsNull() throws Exception {
		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest(FNR, null, null, TOM));

		assertResponse(response);
	}

	@Test
	public void shouldGetAllBrukervarselForBrukerTOMIsNull() throws Exception {
		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest(FNR, null, FOM, null));

		assertResponse(response);
	}

	@Test
	public void shouldGetAllBrukervarselForBrukerPeriodeIsNull() throws Exception {
		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest(FNR, null, null, null));

		assertResponse(response);
	}

	@Test
	public void shouldHandleMultipleVarselbestillingerAndVarsel() throws Exception {
		varselbestillingRepo.deleteAll();
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varsels(createVarselUnique(), createVarselUnique(), createVarselUnique()).build());
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varsels(createVarselUnique(), createVarselUnique(), createVarselUnique()).build());

		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest(FNR, null, FOM, TOM));
		assertThat(response.getBrukervarsel().getVarselbestillingListe().size(), is(2));
		response.getBrukervarsel().getVarselbestillingListe().forEach(vb -> assertThat(vb.getVarselListe(), hasSize(3)));
	}

	@Test
	public void shoulNotReturnNonFerdigstiltVarselOrOutsideTimeframe() throws Exception {
		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varsels(
						createVarselBuilder().status(OPPRETTET).build(),
						createVarselBuilder().status(SENDT).build(),
						createVarselBuilder().status(FEILET).build()
				)
				.build());

		varselbestillingRepo.save(createVarselbestillingBuilder()
				.varsels(createVarselUnique())
				.bestillingTidspunkt(FOM.minusDays(1)).build());

		HentVarselForBrukerResponse response = brukervarselV1
				.hentVarselForBruker(createRequest(FNR, null, FOM, TOM));
		assertResponse(response);

		// Assert that database is unchanged
		assertThat(varselbestillingRepo.findAll(), hasSize(3));
		assertThat(varselRepo.findAll(), hasSize(5));
	}

	private void assertResponse(HentVarselForBrukerResponse response) {
		assertThat(response.getBrukervarsel(), notNullValue());
		assertThat(response.getBrukervarsel().getVarselbestillingListe(), notNullValue());
		assertThat(response.getBrukervarsel().getVarselbestillingListe().size(), is(1));

		Varselbestilling varselbestilling = response.getBrukervarsel().getVarselbestillingListe().get(0);
		assertThat(varselbestilling.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(varselbestilling.getPerson().getIdent(), is(FNR));
		assertThat(varselbestilling.getAktoerId().getAktoerId(), is(AKTOR_ID));
		assertThat(varselbestilling.getBestilt(), is(toXmlGregorianCalendar(BESTILLING_TIDSPUNKT)));
		assertThat(varselbestilling.getReVarselingsintervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getSisteVarselutsendelse(), is(toXmlGregorianCalendar(DISTRIBUSJON_TIDSPUNKT)));
		assertThat(varselbestilling.getVarselListe(), hasSize(1));

		Varsel varsel = varselbestilling.getVarselListe().get(0);
		assertThat(varsel.getKanal(), is(KANAL_CODE.toString()));
		assertThat(varsel.getSendt(), is(toXmlGregorianCalendar(SENDT_TIDSPUNKT)));
		assertThat(varsel.getDistribuert(), is(toXmlGregorianCalendar(DISTRIBUSJON_TIDSPUNKT)));
		assertThat(varsel.getKontaktinfo(), is(KONTAKT_INFO));
		assertThat(varsel.getVarseltittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarseltekst(), is(VARSEL_TEKST));
		assertThat(varsel.getVarselURL(), is(VARSEL_URL));
		assertThat(varsel.isReVarsel(), is(ER_REVARSEL));
	}

	private HentVarselForBrukerRequest createRequest(String fnr, String aktoerId, LocalDateTime fom, LocalDateTime tom) {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();

		if (fnr != null) {
			request.setBruker(new Person());
			((Person) request.getBruker()).setIdent(fnr);
		} else {
			request.setBruker(new AktoerId());
			((AktoerId) request.getBruker()).setAktoerId(aktoerId);
		}

		request.setPeriode(new Periode());
		request.getPeriode().setFom(toXmlGregorianCalendar(fom));
		request.getPeriode().setTom(toXmlGregorianCalendar(tom));

		return request;
	}
}
