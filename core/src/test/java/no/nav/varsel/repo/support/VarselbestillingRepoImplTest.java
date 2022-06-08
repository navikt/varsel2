package no.nav.varsel.repo.support;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.support.VarselbestillingRepoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Lars Aune
 */
@ExtendWith({MockitoExtension.class})
public class VarselbestillingRepoImplTest {
	private static final LocalDateTime FOM_DATE = LocalDateTime.of(2016, Month.JUNE, 1, 13, 0);
	private static final LocalDateTime TOM_DATE = LocalDateTime.of(2016, Month.JULY, 1, 14, 0);
	public static final String BRUKER = "BRUKER";
	public static final String ORG_NR = "ORG_NR";
	@Mock
	private EntityManager entityManager;

	@InjectMocks
	private VarselbestillingRepoImpl varselbestillingRepo;

	private Varselbestilling varselbestillingWithoutOrgNrWithoutFerdigBehandletStatus;
	private Varselbestilling varselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet;
	private Varselbestilling varselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet;
	private Varselbestilling varselbestillingWithOrgNrWithStatuFerdigbehandlet;
	private Set<Varsel> varselsWithStatusFerdigbehandlet;
	private Set<Varsel> varselsWithoutStatusFerdigbehandlet;


	@BeforeEach
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
	public void givesVarselbestillingerWithoutOrgNrAndAtLeastOneVarselWithStatusFerdigbehandlet() {
		TypedQuery query = mock(TypedQuery.class);
		List<Varselbestilling> resultList = new ArrayList<>();
		resultList.add(varselbestillingWithoutOrgNrWithoutFerdigBehandletStatus);
		resultList.add(varselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet);
		resultList.add(varselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet);
		resultList.add(varselbestillingWithOrgNrWithStatuFerdigbehandlet);
		when(query.getResultList()).thenReturn(resultList);
		when(entityManager.createQuery(anyString(), any())).thenReturn(query);
		when(query.setParameter(anyString(), anyString())).thenReturn(query);
		List<Varselbestilling> varselbestillinger =
				varselbestillingRepo.findFerdigbehandletVarselbestillinger(BRUKER, FOM_DATE, TOM_DATE);
		assertThat(varselbestillinger.size(), is(1));
		assertThat(varselbestillinger.get(0), is(varselbestillingWithoutOrgNrAndWithStatusFerdigbehandlet));
	}

	private Varselbestilling mockVarselbestillingWithOrgNrWithStatuFerdigbehandlet() {
		Varselbestilling result = mock(Varselbestilling.class);
		when(result.getOrgNr()).thenReturn(ORG_NR);
		return result;
	}

	private Varselbestilling mockVarselbestillingWithOrgNrWithoutVarselStatusFerdigbehandlet() {
		Varselbestilling result = mock(Varselbestilling.class);
		when(result.getOrgNr()).thenReturn(ORG_NR);
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
		return result;
	}

	private Set<Varsel> createVarselsWithStatusFerdigbehandlet() {
		Set<Varsel> result = new HashSet<>();
		result.add(mockVarselWithStatusFerdigbehandlet());
		return result;
	}

	private Varsel mockVarselWithStatusFerdigbehandlet() {
		Varsel result = mock(Varsel.class);
		when(result.getStatus()).thenReturn(StatusCode.FERDIGBEHANDLET);
		return result;
	}
}