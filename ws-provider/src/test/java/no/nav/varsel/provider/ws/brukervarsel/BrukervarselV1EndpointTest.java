package no.nav.varsel.provider.ws.brukervarsel;

import static no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint.ACCESS_DENIED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.ws.brukervarsel.support.BrukervarselV1Provider;
import no.nav.varsel.provider.ws.brukervarsel.support.HentVarselForBrukerRequestValidator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link BrukervarselV1Endpoint}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class BrukervarselV1EndpointTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private BrukervarselV1Provider brukervarselV1ProviderMock;
	@Mock
	private HentVarselForBrukerRequestValidator validatorMock;

	@InjectMocks
	private BrukervarselV1Endpoint brukervarselV1Endpoint;

	@BeforeClass
	public static void setUpSecurity() throws Exception {
		System.setProperty("no.nav.modig.security.systemuser.username", "Varsel");
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
	}

	@Test
	public void shouldCallValidatorAndProvider_hentVarselForBruker() throws Exception {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		HentVarselForBrukerResponse response = new HentVarselForBrukerResponse();
		when(brukervarselV1ProviderMock.hentVarselForBruker(request)).thenReturn(response);

		HentVarselForBrukerResponse responseFromEndpoint = brukervarselV1Endpoint.hentVarselForBruker(request);

		assertThat(responseFromEndpoint, is(response));
		verify(validatorMock).validate(request);
	}

	@Test
	public void shouldGiveUndetailedExceptionWhenAccessDenied() throws Exception {
		when(brukervarselV1ProviderMock.hentVarselForBruker(any(HentVarselForBrukerRequest.class)))
				.thenThrow(new AuthorizationException("detailed exception"));

		expectedException.expect(AuthorizationException.class);
		expectedException.expectMessage(ACCESS_DENIED);

		brukervarselV1Endpoint.hentVarselForBruker(new HentVarselForBrukerRequest());
	}

}