package no.nav.varsel.repo.support;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit-test for TVARSEL005RepoImpl
 * @author Lars Aune
 */
@RunWith(MockitoJUnitRunner.class)
public class TVARSEL005RepoImplTest {
	public static final int NUMBER_OF_DAYS_STANDARD_OFFSET = 10;
	public static final String BRUKER_ID = "brukerId";
	@Mock
	private EntityManager entityManagerMock;

	@Mock
	private TypedQuery<Varselbestilling> typedQueryMock;

	@InjectMocks
	private TVARSEL005RepoImpl tvarsel005Repo;

	private LocalDateTime datoFom;
	private LocalDateTime datoTom;
	private Varselbestilling ferdigbehandletVarselbestilling;
	private Varselbestilling opprettetVarselbestilling;
	private Varselbestilling varselbestillingMedVarslerDerMinstEttErFerdigbehandlet;
	private Varselbestilling varselbestillingUtenVarsler;
	private ArrayList<Varselbestilling> varebestillinger;

	@Before
	public void onSetup() {
		datoFom = LocalDateTime.now().minusDays(NUMBER_OF_DAYS_STANDARD_OFFSET);
		datoTom = LocalDateTime.now().plusDays(NUMBER_OF_DAYS_STANDARD_OFFSET);
		ferdigbehandletVarselbestilling = createFerdigbehandletVarselbestilling();
		opprettetVarselbestilling = createOpprettetVarselbestilling();
		varselbestillingMedVarslerDerMinstEttErFerdigbehandlet = createVarselBestillingMedMinstEttFerdigbehandletVarsel();
		varselbestillingUtenVarsler = createVarselbestillingUtenVarsler();
		varebestillinger = new ArrayList<>();
		varebestillinger.add(ferdigbehandletVarselbestilling);
		varebestillinger.add(opprettetVarselbestilling);
		varebestillinger.add(varselbestillingMedVarslerDerMinstEttErFerdigbehandlet);
		varebestillinger.add(varselbestillingUtenVarsler);
	}

	@Test
	public void findFerdigbehandletVarselbestillingerThrowsIllegalArgumentExceptionWhenBrukerParameterIsNull() {
		assertThat(tvarsel005Repo.findFerdigbehandletVarselbestillinger(null, datoFom, datoTom), Matchers.empty());
	}

	@Test
	public void findFerdigbehandletVarselBestillingNaarDatoFomOgDatoTomErSatt() {

		doReturn(varebestillinger).when(typedQueryMock).getResultList();
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("datoTom", datoTom);
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("datoFom", datoFom);
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("bruker", BRUKER_ID);
		doReturn(typedQueryMock).when(entityManagerMock).createQuery(anyString(), anyObject());

		List<Varselbestilling> varselbestillinger =
				tvarsel005Repo.findFerdigbehandletVarselbestillinger(BRUKER_ID, datoFom, datoTom);

		assertThat(varselbestillinger,
				Matchers.contains(ferdigbehandletVarselbestilling, varselbestillingMedVarslerDerMinstEttErFerdigbehandlet));

		verify(typedQueryMock).setParameter("bruker", BRUKER_ID);
		verify(typedQueryMock).setParameter("datoFom", datoFom);
		verify(typedQueryMock).setParameter("datoTom", datoTom);
		verify(typedQueryMock,times(3)).setParameter(anyString(), anyObject());

		reset(entityManagerMock);
		reset(typedQueryMock);
	}

	@Test
	public void findFerdigbehandletVarselBestillingNaarBareDatoFomErSatt() {

		doReturn(varebestillinger).when(typedQueryMock).getResultList();
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("datoFom", datoFom);
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("bruker", BRUKER_ID);
		doReturn(typedQueryMock).when(entityManagerMock).createQuery(anyString(), anyObject());

		List<Varselbestilling> varselbestillinger =
				tvarsel005Repo.findFerdigbehandletVarselbestillinger(BRUKER_ID, datoFom, null);

		assertThat(varselbestillinger,
				Matchers.contains(ferdigbehandletVarselbestilling, varselbestillingMedVarslerDerMinstEttErFerdigbehandlet));

		verify(typedQueryMock).setParameter("bruker", BRUKER_ID);
		verify(typedQueryMock).setParameter("datoFom", datoFom);
		verify(typedQueryMock,times(2)).setParameter(anyString(), anyObject());

		reset(entityManagerMock);
		reset(typedQueryMock);
	}

	@Test
	public void findFerdigbehandletVarselBestillingNaarBareDatoTomErSatt() {

		doReturn(varebestillinger).when(typedQueryMock).getResultList();
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("datoTom", datoTom);
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("bruker", BRUKER_ID);
		doReturn(typedQueryMock).when(entityManagerMock).createQuery(anyString(), anyObject());

		List<Varselbestilling> varselbestillinger =
				tvarsel005Repo.findFerdigbehandletVarselbestillinger(BRUKER_ID, null, datoTom);

		assertThat(varselbestillinger,
				Matchers.contains(ferdigbehandletVarselbestilling, varselbestillingMedVarslerDerMinstEttErFerdigbehandlet));

		verify(typedQueryMock).setParameter("bruker", BRUKER_ID);
		verify(typedQueryMock).setParameter("datoTom", datoTom);
		verify(typedQueryMock,times(2)).setParameter(anyString(), anyObject());

		reset(entityManagerMock);
		reset(typedQueryMock);
	}

	@Test
	public void findFerdigbehandletVarselBestillingWhenNoDateParameters() {

		doReturn(varebestillinger).when(typedQueryMock).getResultList();
		doReturn(typedQueryMock).when(typedQueryMock).setParameter("bruker", BRUKER_ID);
		doReturn(typedQueryMock).when(entityManagerMock).createQuery(anyString(), anyObject());

		List<Varselbestilling> varselbestillinger =
				tvarsel005Repo.findFerdigbehandletVarselbestillinger(BRUKER_ID, null, null);

		assertThat(varselbestillinger,
				Matchers.contains(ferdigbehandletVarselbestilling, varselbestillingMedVarslerDerMinstEttErFerdigbehandlet));

		verify(typedQueryMock).setParameter("bruker", BRUKER_ID);
		verify(typedQueryMock,times(1)).setParameter(anyString(), anyObject());

		reset(entityManagerMock);
		reset(typedQueryMock);
	}

	private Varselbestilling createFerdigbehandletVarselbestilling() {
		Varselbestilling result = new Varselbestilling();
		Varsel ferdigbehandletVarsel1 = createFerdigbehandletVarsel();
		result.addVarsel(ferdigbehandletVarsel1);
		Varsel ferdigbehandletVarsel2 = createFerdigbehandletVarsel();
		result.addVarsel(ferdigbehandletVarsel2);
		return result;
	}

	private Varselbestilling createOpprettetVarselbestilling() {
		Varselbestilling result = new Varselbestilling();
		Varsel ferdigbehandletVarsel1 = createOpprettetVarsel();
		result.addVarsel(ferdigbehandletVarsel1);
		Varsel ferdigbehandletVarsel2 = createOpprettetVarsel();
		result.addVarsel(ferdigbehandletVarsel2);
		return result;
	}

	private Varselbestilling createVarselBestillingMedMinstEttFerdigbehandletVarsel() {
		Varselbestilling result = new Varselbestilling();
		Varsel ferdigbehandletVarsel1 = createOpprettetVarsel();
		result.addVarsel(ferdigbehandletVarsel1);
		Varsel ferdigbehandletVarsel2 = createFerdigbehandletVarsel();
		result.addVarsel(ferdigbehandletVarsel2);
		return result;
	}

	private Varselbestilling createVarselbestillingUtenVarsler() {
		return new Varselbestilling();
	}


	private Varsel createFerdigbehandletVarsel() {
		Varsel result = new Varsel();
		result.setStatus(StatusCode.FERDIGBEHANDLET);
		return result;
	}

	private Varsel createOpprettetVarsel() {
		Varsel result = new Varsel();
		result.setStatus(StatusCode.OPPRETTET);
		return result;
	}

}