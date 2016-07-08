package no.nav.varsel.service;

import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo.Builder.aHentVarselForBrukerTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.tvarsel005.support.BrukervarselMapper;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Unit test for {@link BrukervarselV1Service} TVARSEL005
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class BrukervarselV1ServiceTest {

	private static final LocalDateTime DATO_TOM = LocalDateTime.now().plusDays(1);
	private static final LocalDateTime DATO_FOM = LocalDateTime.now().minusDays(1);
	private static final String BRUKER = "bruker";

	private final ArrayList<Varselbestilling> varselbestillings = Lists.newArrayList(new Varselbestilling());
	private final HentVarselForBrukerResponseTo responseTo = new HentVarselForBrukerResponseTo();

	@Mock
	private VarselbestillingRepo varselbestillingRepoMock;
	@Mock
	private BrukervarselMapper brukervarselMapperMock;
	@InjectMocks
	private BrukervarselV1Service brukervarselV1Service;

	@Before
	public void setUp() throws Exception {
		when(varselbestillingRepoMock.findFerdigbehandletVarselbestillinger(BRUKER, DATO_FOM, DATO_TOM))
				.thenReturn(varselbestillings);
		when(brukervarselMapperMock.map(varselbestillings)).thenReturn(responseTo);
	}

	@Test
	public void shouldHentAndMapFromFnr() throws Exception {
		HentVarselForBrukerResponseTo response = brukervarselV1Service
				.hentVarselForBruker(aHentVarselForBrukerTo()
						.datoFom(DATO_FOM).datoTom(DATO_TOM).fnr(BRUKER).build());

		assertThat(response, is(responseTo));
	}

	@Test
	public void shouldHentAndMapFromAktoerId() throws Exception {
		HentVarselForBrukerResponseTo response = brukervarselV1Service
				.hentVarselForBruker(aHentVarselForBrukerTo()
						.datoFom(DATO_FOM).datoTom(DATO_TOM).aktoerId(BRUKER).build());

		assertThat(response, is(responseTo));
	}
}