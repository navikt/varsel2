package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSAktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

import static java.util.Calendar.JUNE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HentVarselForBrukerRequestMapperTest {

	public static final String AKTOER_ID = "AKTOER_ID";
	public static final String FNR = "FNR";
	private final HentVarselForBrukerRequestMapper mapper = new HentVarselForBrukerRequestMapper();
	private XMLGregorianCalendar firstOfJune2016;
	private XMLGregorianCalendar twentiethOfJune2016;
	private LocalDateTime twentiethOfJune2016LocalDateTime;
	private LocalDateTime firstOfJune2016LocalDateTime;

	@BeforeEach
	public void onSetup() {
		firstOfJune2016 = TestdataUtil.getXMLGregorianCalendar(2016, JUNE, 1);
		twentiethOfJune2016 = TestdataUtil.getXMLGregorianCalendar(2016, JUNE, 20);
		twentiethOfJune2016LocalDateTime = LocalDateTime.of(2016, 6, 20, 0, 0, 0, 0);
		firstOfJune2016LocalDateTime = LocalDateTime.of(2016, 6, 1, 0, 0, 0, 0);
	}

	@Test
	public void throwsIllegalArgumentExceptionWhenNullIsParameter() {
		assertThrows(IllegalArgumentException.class, () -> mapper.map(null));
	}

	@Test
	public void shouldMapAktoerId() {
		WSHentVarselForBrukerRequest request = createRequestWithAktoerId();
		assignEmptyPeriode(request);

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getAktoerId(), is(AKTOER_ID));
		assertThat(hentVarselForBrukerTo.getFnr(), nullValue());
	}

	private void assignEmptyPeriode(WSHentVarselForBrukerRequest request) {
		request.setPeriode(createPeriode(null, null));
	}

	@Test
	public void shouldMapFnr() {
		WSHentVarselForBrukerRequest request = createRequestWithFnr();
		assignEmptyPeriode(request);

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getAktoerId(), nullValue());
		assertThat(hentVarselForBrukerTo.getFnr(), is(FNR));
	}

	@Test
	public void shouldMapFom() {
		WSHentVarselForBrukerRequest request = createRequestWithFnr();
		request.setPeriode(createPeriode(this.firstOfJune2016, null));

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getDatoFom(), is(firstOfJune2016LocalDateTime));
		assertThat(hentVarselForBrukerTo.getDatoTom(), nullValue());
	}

	@Test
	public void shouldMapTom() {
		WSHentVarselForBrukerRequest request = createRequestWithFnr();
		request.setPeriode(createPeriode(null, this.twentiethOfJune2016));

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getDatoFom(), nullValue());
		assertThat(hentVarselForBrukerTo.getDatoTom(), is(twentiethOfJune2016LocalDateTime));
	}

	@Test
	public void shouldMapFomANdTom() {
		WSHentVarselForBrukerRequest request = createRequestWithFnr();
		request.setPeriode(createPeriode(this.firstOfJune2016, this.twentiethOfJune2016));

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getDatoFom(), is(firstOfJune2016LocalDateTime));
		assertThat(hentVarselForBrukerTo.getDatoTom(), is(twentiethOfJune2016LocalDateTime));
	}

	@Test
	public void shouldHandleNullPeriode() {
		WSHentVarselForBrukerRequest request = createRequestWithFnr();
		request.setPeriode(null);

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);
		assertThat(hentVarselForBrukerTo.getFnr(), is(FNR));
	}

	private WSHentVarselForBrukerRequest createRequestWithAktoerId() {
		WSHentVarselForBrukerRequest request = new WSHentVarselForBrukerRequest();
		WSAktoerId bruker = new WSAktoerId();
		bruker.setAktoerId(AKTOER_ID);
		request.setBruker(bruker);

		return request;
	}

	private WSHentVarselForBrukerRequest createRequestWithFnr() {
		WSHentVarselForBrukerRequest request = new WSHentVarselForBrukerRequest();
		WSPerson bruker = new WSPerson();
		bruker.setIdent(FNR);
		request.setBruker(bruker);

		return request;
	}

	private WSPeriode createPeriode(XMLGregorianCalendar fom, XMLGregorianCalendar tom) {
		WSPeriode periode = new WSPeriode();
		periode.setFom(fom);
		periode.setTom(tom);

		return periode;
	}
}