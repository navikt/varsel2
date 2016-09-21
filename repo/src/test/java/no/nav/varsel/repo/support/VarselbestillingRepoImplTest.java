package no.nav.varsel.repo.support;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lars Aune
 */
@RunWith(MockitoJUnitRunner.class)
public class VarselbestillingRepoImplTest {
	private static final LocalDateTime FOM_DATE = LocalDateTime.of(2016, Month.JUNE, 1, 13, 0);
	private static final LocalDateTime TOM_DATE = LocalDateTime.of(2016, Month.JULY, 1, 14, 0);
	public static final String BRUKER = "BRUKER";
	public static final String ORG_NR = "ORG_NR";
	@Mock
	private EntityManager entityManager;

	@InjectMocks
	private VarselbestillingRepoImpl varselbestillingRepo;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private Varselbestilling varselbestillingWithoutOrgNrWithoutFerdigBehandletStatus;
	private Varselbestilling varselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet;
	private Varselbestilling varselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet;
	private Varselbestilling varselbestillingWithOrgNrWithStatuFerdigbehandlet;
	private Set<Varsel> varselsWithStatusFerdigbehandlet;
	private Set<Varsel> varselsWithoutStatusFerdigbehandlet;


	@Before
	public void onSetupOfTest() {
		varselsWithStatusFerdigbehandlet = createVarselsWithStatusFerdigbehandlet();
		varselsWithoutStatusFerdigbehandlet = createVarselsWithoutStatusFerdigbehandlet();

		varselbestillingWithoutOrgNrWithoutFerdigBehandletStatus =
				mockVarselbestillingWithoutOrgNrWithoutFerdigBehandletStatus();

		varselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet =
				mockVarselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet();

		varselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet =
				mockVarselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet();

		varselbestillingWithOrgNrWithStatuFerdigbehandlet =
				mockVarselbestillingWithOrgNrWithStatuFerdigbehandlet();

	}

	@Test
	public void throwsIllegalArgumentExceptionWhenBrukerParameterIsNull() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("bruker is null or empty");
		varselbestillingRepo.findFerdigbehandletVarselbestillinger(null, FOM_DATE, TOM_DATE);
	}

	@Test
	public void throwsIllegalArgumentExceptionWhenBrukerParameterIsEmpty() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("bruker is null or empty");
		varselbestillingRepo.findFerdigbehandletVarselbestillinger("", FOM_DATE, TOM_DATE);
	}

	@Test
	public void givesVarselbestillingerWithoutOrgNrAndAtLeastOneVarselWithStatusFerdigbehandlet() {
		TypedQuery query = mock(TypedQuery.class);
		List<Varselbestilling> resultList = new ArrayList<>();
		resultList.add(varselbestillingWithoutOrgNrWithoutFerdigBehandletStatus);
		resultList.add(varselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet);
		resultList.add(varselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet);
		resultList.add(varselbestillingWithOrgNrWithStatuFerdigbehandlet);
		when(query.getResultList()).thenReturn(resultList);
		when(entityManager.createQuery(anyString(), anyObject()))
				.thenReturn(query);
		when(query.setParameter(anyString(), anyObject())).thenReturn(query);
		List<Varselbestilling> varselbestillinger =
				varselbestillingRepo.findFerdigbehandletVarselbestillinger(BRUKER, FOM_DATE, TOM_DATE);
		assertThat(varselbestillinger.size(), is(1));
		assertThat(varselbestillinger.get(0), is(varselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet));
	}

	private Varselbestilling mockVarselbestillingWithOrgNrWithStatuFerdigbehandlet() {
		Varselbestilling result = mock(Varselbestilling.class);
		when(result.getOrgNr()).thenReturn(ORG_NR);
		when(result.getVarsels()).thenReturn(varselsWithStatusFerdigbehandlet);
		return result;
	}

	private Varselbestilling mockVarselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet() {
		Varselbestilling result = mock(Varselbestilling.class);
		when(result.getOrgNr()).thenReturn(ORG_NR);
		when(result.getVarsels()).thenReturn(varselsWithoutStatusFerdigbehandlet);
		return result;
	}

	private Varselbestilling mockVarselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet() {
		Varselbestilling result = mock(Varselbestilling.class);
		when(result.getOrgNr()).thenReturn(null);
		when(result.getVarsels()).thenReturn(varselsWithStatusFerdigbehandlet);
		return result;
	}

	private Varselbestilling mockVarselbestillingWithoutOrgNrWithoutFerdigBehandletStatus() {
		Varselbestilling result = mock(Varselbestilling.class);
		when(result.getOrgNr()).thenReturn(null);
		when(result.getVarsels()).thenReturn(varselsWithoutStatusFerdigbehandlet);
		return result;
	}

	private Set<Varsel> createVarselsWithoutStatusFerdigbehandlet() {
		Set<Varsel> result = new HashSet<>();
		result.add(mockVarselWithoutStatus());
		result.add(mockVarselWithStatusOpprettet());
		return result;
	}

	private Set<Varsel> createVarselsWithStatusFerdigbehandlet() {
		Set<Varsel> result = new HashSet<>();
		result.add(mockVarselWithoutStatus());
		result.add(mockVarselWithStatusFerdigbehandlet());
		result.add(mockVarselWithStatusOpprettet());
		return result;
	}

	private Varsel mockVarselWithoutStatus() {
		Varsel result = mock(Varsel.class);
		when(result.getStatus()).thenReturn(null);
		return result;
	}

	private Varsel mockVarselWithStatusOpprettet() {
		Varsel result = mock(Varsel.class);
		when(result.getStatus()).thenReturn(StatusCode.OPPRETTET);
		return result;
	}

	private Varsel mockVarselWithStatusFerdigbehandlet() {
		Varsel result = mock(Varsel.class);
		when(result.getStatus()).thenReturn(StatusCode.FERDIGBEHANDLET);
		return result;
	}
}