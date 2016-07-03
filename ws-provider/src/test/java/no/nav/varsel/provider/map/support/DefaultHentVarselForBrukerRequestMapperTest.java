package no.nav.varsel.provider.map.support;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.provider.map.HentVarselForBrukerRequestMapper;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * @author Lars Aune
 */
public class DefaultHentVarselForBrukerRequestMapperTest {
	public static final String AKTOER_ID = "AKTOER_ID";
	public static final String FNR = "FNR";
	private HentVarselForBrukerRequestMapper mapper = new DefaultHentVarselForBrukerRequestMapper();
	private XMLGregorianCalendar firstOfJune2016;
	private XMLGregorianCalendar twentiethOfJune2016;
	private LocalDateTime twentiethOfJune2016LocalDateTime;
	private LocalDateTime firstOfJune2016LocalDateTime;

	@Before
	public void onSetup() {
		firstOfJune2016 = TestdataUtil.getXMLGregorianCalendar(2016, Calendar.JUNE, 1);
		twentiethOfJune2016 = TestdataUtil.getXMLGregorianCalendar(2016, Calendar.JUNE, 20);
		twentiethOfJune2016LocalDateTime = LocalDateTime.of(2016,6, 20, 0, 0, 0, 0);
		firstOfJune2016LocalDateTime = LocalDateTime.of(2016, 6, 1, 0, 0, 0, 0);
	}
	@Test(expected = IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionWhenNullIsParameter() {
		mapper.map(null);
	}

	@Test
	public void shouldMapAktoerId() {
		HentVarselForBrukerRequest request = createRequestWithAktoerId();
		assignEmptyPeriode(request);

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getAktoerId(), is(AKTOER_ID));
		assertThat(hentVarselForBrukerTo.getFnr(), nullValue());
	}

	private void assignEmptyPeriode(HentVarselForBrukerRequest request) {
		request.setPeriode(createPeriode(null, null));
	}

	@Test
	public void shouldMapFnr() {
		HentVarselForBrukerRequest request = createRequestWithFnr();
		assignEmptyPeriode(request);

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getAktoerId(), nullValue());
		assertThat(hentVarselForBrukerTo.getFnr(), is(FNR));
	}

	@Test
	public void shouldMapFom() {
		HentVarselForBrukerRequest request = createRequestWithFnr();
		request.setPeriode(createPeriode(this.firstOfJune2016, null));

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getDatoFom(), is(firstOfJune2016LocalDateTime));
		assertThat(hentVarselForBrukerTo.getDatoTom(), nullValue());
	}

	@Test
	public void shouldMapTom() {
		HentVarselForBrukerRequest request = createRequestWithFnr();
		request.setPeriode(createPeriode(null, this.twentiethOfJune2016));

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getDatoFom(), nullValue());
		assertThat(hentVarselForBrukerTo.getDatoTom(), is(twentiethOfJune2016LocalDateTime));
	}

	@Test
	public void shouldMapFomANdTom() {
		HentVarselForBrukerRequest request = createRequestWithFnr();
		request.setPeriode(createPeriode(this.firstOfJune2016, this.twentiethOfJune2016));

		HentVarselForBrukerTo hentVarselForBrukerTo = mapper.map(request);

		assertThat(hentVarselForBrukerTo.getDatoFom(), is(firstOfJune2016LocalDateTime));
		assertThat(hentVarselForBrukerTo.getDatoTom(), is(twentiethOfJune2016LocalDateTime));
	}


	private HentVarselForBrukerRequest createRequestWithAktoerId() {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		AktoerId bruker = new AktoerId();
		bruker.setAktoerId(AKTOER_ID);
		request.setBruker(bruker);
		return request;
	}

	private HentVarselForBrukerRequest createRequestWithFnr() {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		Person bruker = new Person();
		bruker.setIdent(FNR);
		request.setBruker(bruker);
		return request;
	}

	private Periode createPeriode(XMLGregorianCalendar fom, XMLGregorianCalendar tom) {
		Periode periode = new Periode();
		periode.setFom(fom);
		periode.setTom(tom);
		return periode;
	}
}