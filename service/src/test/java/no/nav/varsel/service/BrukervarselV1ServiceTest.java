package no.nav.varsel.service;

import com.google.common.collect.Lists;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.tvarsel005.support.BrukervarselMapper;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo.Builder.aHentVarselForBrukerTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

	@BeforeEach
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