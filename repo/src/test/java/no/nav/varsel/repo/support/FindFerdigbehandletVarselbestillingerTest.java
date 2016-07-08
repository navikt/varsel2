package no.nav.varsel.repo.support;

import static java.util.stream.Collectors.toList;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.builder.VarselbestillingBuilder;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.AbstractRepoTest;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit-test for VarselbestillingRepoImpl FindFerdigbehandletVarselbestillingerTest
 *
 * @author Lars Aune
 * @author Andreas Skomedal, Visma Consulting
 */
public class FindFerdigbehandletVarselbestillingerTest extends AbstractRepoTest {

	public static final int NUMBER_OF_DAYS_STANDARD_OFFSET = 10;

	@Inject
	private VarselbestillingRepo varselbestillingRepo;

	private LocalDateTime datoFom = LocalDateTime.now().minusDays(NUMBER_OF_DAYS_STANDARD_OFFSET);
	private LocalDateTime datoTom = LocalDateTime.now().plusDays(NUMBER_OF_DAYS_STANDARD_OFFSET);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void onSetup() {
		varselbestillingRepo.save(createVarselBestilling(createFerdigbehandletVarsel(), createFerdigbehandletVarsel())
				.varselbestillingId("ferdigbehandletVarselbestillingInFarPast")
				.bestillingTidspunkt(LocalDateTime.now().minusDays(NUMBER_OF_DAYS_STANDARD_OFFSET * 2))
				.build());
		varselbestillingRepo.save(createVarselBestilling(createFerdigbehandletVarsel(), createFerdigbehandletVarsel())
				.varselbestillingId("ferdigbehandletVarselbestillingInFarFuture")
				.bestillingTidspunkt(LocalDateTime.now().plusDays(NUMBER_OF_DAYS_STANDARD_OFFSET * 2))
				.build());

		varselbestillingRepo.save(createVarselBestilling(createFerdigbehandletVarsel(), createFerdigbehandletVarsel())
				.varselbestillingId("ferdigbehandletVarselbestilling").build());
		varselbestillingRepo.save(createVarselBestilling(createOpprettetVarsel(), createFerdigbehandletVarsel())
				.varselbestillingId("varselbestillingMedVarslerDerMinstEttErFerdigbehandlet")
				.build());

		varselbestillingRepo.save(createVarselBestilling(createOpprettetVarsel(), createOpprettetVarsel())
				.varselbestillingId("opprettetVarselbestilling")
				.build());
		varselbestillingRepo.save(createVarselBestilling()
				.varselbestillingId("varselbestillingUtenVarsler")
				.build());
	}

	@Test
	public void shouldThrowExceptionWhenBrukerParameterIsNull() {
		expectedException.expect(InvalidDataAccessApiUsageException.class);
		expectedException.expectMessage("bruker is null");
		varselbestillingRepo.findFerdigbehandletVarselbestillinger(null, datoFom, datoTom);
	}

	@Test
	public void shouldNotFindForOtherUser() throws Exception {
		assertThat(varselbestillingRepo.findFerdigbehandletVarselbestillinger("otherId", datoFom, datoTom), hasSize(0));
	}

	@Test
	public void findFerdigbehandletVarselBestillingNaarDatoFomOgDatoTomErSatt() {
		List<Varselbestilling> varselbestillinger =
				varselbestillingRepo.findFerdigbehandletVarselbestillinger(FNR, datoFom, datoTom);

		assertVarselbestillingerIs(varselbestillinger,
				"ferdigbehandletVarselbestilling", "varselbestillingMedVarslerDerMinstEttErFerdigbehandlet");
	}

	@Test
	public void findFerdigbehandletVarselBestillingNaarDatoFomOgDatoTomErSattForAktoerId() {
		List<Varselbestilling> varselbestillinger =
				varselbestillingRepo.findFerdigbehandletVarselbestillinger(AKTOR_ID, datoFom, datoTom);

		assertVarselbestillingerIs(varselbestillinger,
				"ferdigbehandletVarselbestilling", "varselbestillingMedVarslerDerMinstEttErFerdigbehandlet");
	}

	@Test
	public void findFerdigbehandletVarselBestillingNaarBareDatoFomErSatt() {
		List<Varselbestilling> varselbestillinger =
				varselbestillingRepo.findFerdigbehandletVarselbestillinger(FNR, datoFom, null);

		assertVarselbestillingerIs(varselbestillinger,
				"ferdigbehandletVarselbestilling", "ferdigbehandletVarselbestillingInFarFuture",
				"varselbestillingMedVarslerDerMinstEttErFerdigbehandlet");
	}

	@Test
	public void findFerdigbehandletVarselBestillingNaarBareDatoTomErSatt() {
		List<Varselbestilling> varselbestillinger =
				varselbestillingRepo.findFerdigbehandletVarselbestillinger(FNR, null, datoTom);

		assertVarselbestillingerIs(varselbestillinger,
				"ferdigbehandletVarselbestilling", "ferdigbehandletVarselbestillingInFarPast",
				"varselbestillingMedVarslerDerMinstEttErFerdigbehandlet");
	}

	@Test
	public void findFerdigbehandletVarselBestillingWhenNoDateParameters() {
		List<Varselbestilling> varselbestillinger =
				varselbestillingRepo.findFerdigbehandletVarselbestillinger(FNR, null, null);

		assertVarselbestillingerIs(varselbestillinger,
				"ferdigbehandletVarselbestilling", "ferdigbehandletVarselbestillingInFarPast",
				"ferdigbehandletVarselbestillingInFarFuture", "varselbestillingMedVarslerDerMinstEttErFerdigbehandlet");
	}

	private VarselbestillingBuilder createVarselBestilling(Varsel... varsels) {
		return TestdataUtil.createVarselbestillingBuilder()
				.bestillingTidspunkt(LocalDateTime.now())
				.varsels(varsels);
	}

	private Varsel createFerdigbehandletVarsel() {
		return TestdataUtil.createVarselBuilder().status(StatusCode.FERDIGBEHANDLET).build();
	}

	private Varsel createOpprettetVarsel() {
		return TestdataUtil.createVarselBuilder().status(StatusCode.OPPRETTET).build();
	}

	private void assertVarselbestillingerIs(List<Varselbestilling> found, String... expectedIds) {
		List<String> foundIds = found.stream().map(Varselbestilling::getVarselbestillingId).collect(toList());
		assertThat(found.toString(), foundIds, containsInAnyOrder(expectedIds));
	}

}