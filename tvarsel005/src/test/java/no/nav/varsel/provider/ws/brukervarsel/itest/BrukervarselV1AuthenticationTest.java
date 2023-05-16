package no.nav.varsel.provider.ws.brukervarsel.itest;

import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.domain.IdentType;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest;
import no.nav.varsel.provider.AbstractWsProviderITest;
import no.nav.varsel.provider.ws.brukervarsel.AuthorizationException;
import no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static no.nav.modig.core.context.SubjectHandlerUtils.setEksternBruker;
import static no.nav.modig.core.context.SubjectHandlerUtils.setInternBruker;
import static no.nav.modig.core.context.SubjectHandlerUtils.setSystemressurs;
import static no.nav.modig.core.domain.IdentType.Prosess;
import static no.nav.modig.core.domain.IdentType.Samhandler;
import static no.nav.modig.core.domain.IdentType.Sikkerhet;
import static no.nav.varsel.domain.Constants.USER_ID;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrukervarselV1AuthenticationTest extends AbstractWsProviderITest {

	@Autowired
	private BrukervarselV1Endpoint brukervarselV1;

	@Test
	public void shouldAllowAccessToInternbruker() throws Exception {
		setInternBruker(USER_ID);

		brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest());
	}

	@Test
	public void shouldDenyAccessToEksternbruker() {
		setEksternBruker(USER_ID, 4, "");

		expectAuthException();
	}

	@Test
	public void shouldDenyAccessToSystemRessurs() {
		setSystemressurs(USER_ID);

		expectAuthException();
	}

	@Test
	public void shouldDenyAccessToSamhandler() {
		setupSubjectWithIdentType(Samhandler);

		expectAuthException();
	}

	@Test
	public void shouldDenyAccessToSikkerhet() {
		setupSubjectWithIdentType(Sikkerhet);

		expectAuthException();
	}

	@Test
	public void shouldDenyAccessToProsess()  {
		setupSubjectWithIdentType(Prosess);

		expectAuthException();
	}

	private WSHentVarselForBrukerRequest createHentVarselForBrukerRequest() {
		WSHentVarselForBrukerRequest request = new WSHentVarselForBrukerRequest();

		request.setBruker(new WSPerson());
		((WSPerson) request.getBruker()).setIdent(USER_ID);

		request.setPeriode(new WSPeriode());
		request.getPeriode().setFom(toXmlGregorianCalendar(LocalDateTime.now()));
		request.getPeriode().setTom(toXmlGregorianCalendar(LocalDateTime.now()));
		return request;
	}

	private void setupSubjectWithIdentType(IdentType identType) {
		SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(USER_ID, identType).getSubject());
	}

	private void expectAuthException() {
		Exception e = assertThrows(AuthorizationException.class, () -> brukervarselV1.hentVarselForBruker(createHentVarselForBrukerRequest()));
		assertEquals("Access denied", e.getMessage());
	}
}
