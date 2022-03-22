package no.nav.varsel.provider.ws.brukervarsel.itest;

import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.domain.IdentType;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.provider.AbstractWsProviderITest;
import no.nav.varsel.provider.ws.brukervarsel.AuthorizationException;
import no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

import static no.nav.varsel.domain.Constants.USER_ID;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.hamcrest.core.Is.is;

/**
 * Tests the XACML-logic in BrukervarselV1 TVARSEL005
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class BrukervarselV1AuthenticationTest extends AbstractWsProviderITest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired
	private BrukervarselV1Endpoint brukervarselV1;

	@Test
	public void shouldAllowAccessToInternbruker() throws Exception {
		SubjectHandlerUtils.setInternBruker(USER_ID);

		brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest());
	}

	@Test
	public void shouldDenyAccessToEksternbruker() throws Exception {
		SubjectHandlerUtils.setEksternBruker(USER_ID, 4, "");

		expectAuthException();

		brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest());
	}

	@Test
	public void shouldDenyAccessToSystemRessurs() throws Exception {
		SubjectHandlerUtils.setSystemressurs(USER_ID);

		expectAuthException();

		brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest());
	}

	@Test
	public void shouldDenyAccessToSamhandler() throws Exception {
		setupSubjectWithIdentType(IdentType.Samhandler);

		expectAuthException();

		brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest());
	}

	@Test
	public void shouldDenyAccessToSikkerhet() throws Exception {
		setupSubjectWithIdentType(IdentType.Sikkerhet);

		expectAuthException();

		brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest());
	}

	@Test
	public void shouldDenyAccessToProsess() throws Exception {
		setupSubjectWithIdentType(IdentType.Prosess);

		expectAuthException();

		brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest());
	}

	private HentVarselForBrukerRequest createHentVarselForBrukerRequest() {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();

		request.setBruker(new Person());
		((Person) request.getBruker()).setIdent(USER_ID);

		request.setPeriode(new Periode());
		request.getPeriode().setFom(toXmlGregorianCalendar(LocalDateTime.now()));
		request.getPeriode().setTom(toXmlGregorianCalendar(LocalDateTime.now()));
		return request;
	}

	private void setupSubjectWithIdentType(IdentType identType) {
		SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(USER_ID, identType).getSubject());
	}

	private void expectAuthException() {
		expectedException.expectMessage(is("Access denied"));
		expectedException.expect(AuthorizationException.class);
	}
}
