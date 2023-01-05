package no.nav.varsel.provider.ws.brukervarsel;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.ws.brukervarsel.support.BrukervarselV1Provider;
import no.nav.varsel.provider.ws.brukervarsel.support.HentVarselForBrukerRequestValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static no.nav.varsel.provider.ws.brukervarsel.BrukervarselV1Endpoint.ACCESS_DENIED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrukervarselV1EndpointTest {

	@Mock
	private BrukervarselV1Provider brukervarselV1ProviderMock;
	@Mock
	private HentVarselForBrukerRequestValidator validatorMock;

	@InjectMocks
	private BrukervarselV1Endpoint brukervarselV1Endpoint;

	@BeforeAll
	public static void setUpSecurity() {
		System.setProperty("varsel.serviceuser.username", "Varsel");
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
				.thenThrow(new AuthorizationException(ACCESS_DENIED));

		Exception e = assertThrows(AuthorizationException.class, () -> brukervarselV1Endpoint.hentVarselForBruker(new HentVarselForBrukerRequest()));
		Assertions.assertEquals(ACCESS_DENIED, e.getMessage());
	}

}