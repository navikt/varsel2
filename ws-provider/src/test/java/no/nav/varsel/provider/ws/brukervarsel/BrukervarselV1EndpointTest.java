package no.nav.varsel.provider.ws.brukervarsel;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.map.support.HentVarselForBrukerRequestValidator;
import no.nav.varsel.provider.ws.brukervarsel.support.BrukervarselV1Provider;
import org.junit.Test;
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

	@Mock
	private BrukervarselV1Provider brukervarselV1ProviderMock;
	@Mock
	private HentVarselForBrukerRequestValidator validatorMock;

	@InjectMocks
	private BrukervarselV1Endpoint brukervarselV1Endpoint;

	@Test
	public void shouldCallValidatorAndProvider_hentVarselForBruker() throws Exception {
		HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
		HentVarselForBrukerResponse response = new HentVarselForBrukerResponse();
		when(brukervarselV1ProviderMock.hentVarselForBruker(request)).thenReturn(response);

		HentVarselForBrukerResponse responseFromEndpoint = brukervarselV1Endpoint.hentVarselForBruker(request);

		assertThat(responseFromEndpoint, is(response));
		verify(validatorMock).validate(request);
	}

}