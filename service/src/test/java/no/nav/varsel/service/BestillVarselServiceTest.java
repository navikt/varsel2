package no.nav.varsel.service;

import static no.nav.varsel.domain.to.AktoerTo.newAktoerId;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.KANAL_CODE;
import static no.nav.varsel.repo.TestdataUtil.NESTE_VARSLING_DATO;
import static no.nav.varsel.repo.TestdataUtil.PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.REVARSLING_INTERVALL;
import static no.nav.varsel.repo.TestdataUtil.UTLOP_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.functional.VarselbestillingAlreadyExistException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingNotExistException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit test for {@link BestillVarselService}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class BestillVarselServiceTest {

	private static final String NEW_BESTILLING_ID = "newBestillingId";

	@Mock
	private VarselbestillingRepo varselbestillingRepoMock;
	@Mock
	private AktoerService aktoerService;

	@Mock
	private VarselInfoConsumer varselInfoConsumer;
	@Mock
	private HentDigitalKontaktinformasjonConsumer dkifConsumer;

	@Mock
	private VarselBestillingDomainMapper domainMapper;
	@Mock
	private VarselutsendingProducer varselutsendingProducer;
	@Mock
	private VarselutsendingToMapper varselutsendingToMapper;

	@InjectMocks
	private BestillVarselService bestillVarselService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private BestillVarselTo bestillingTo;
	private Varselbestilling existingVarselbestilling = new Varselbestilling();
	private Varselbestilling newVarselbestilling = new Varselbestilling();
	private Varsel varsel = new Varsel();
	private VarselutsendingTo varselutsendingTo = new VarselutsendingTo();
	private AktoerTo aktoerTo = newAktoerId(AKTOR_ID);
	private VarselInfoTo varselInfoTo = new VarselInfoTo();
	private KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();

	@Before
	public void setUp() throws Exception {
		bestillingTo = new BestillVarselTo();
		existingVarselbestilling.setFnr(FNR);
		existingVarselbestilling.setRevarslingIntervall(REVARSLING_INTERVALL);
		existingVarselbestilling.setAntallRevarslinger(ANTALL_REVARSLINGER);
		existingVarselbestilling.setNesteVarslingDato(NESTE_VARSLING_DATO);

		when(varselbestillingRepoMock.findByVarselbestillingIdEager(VARSELBESTILLING_ID)).thenReturn(existingVarselbestilling);

		when(aktoerService.findMissingAktoer(bestillingTo)).thenReturn(newPersonIdent(FNR));

		varselInfoTo.setPreferertKanal(PREFERERT_KANAL);
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		kontaktregisterTo.setKanaler(PREFERERT_KANAL);
		when(dkifConsumer.hentDigitalKontaktinformasjonAndDecideKanal(FNR, varselInfoTo.getPreferertKanal()))
				.thenReturn(kontaktregisterTo);
		newVarselbestilling.getVarsels().add(varsel);

		when(domainMapper.mapVarselbestillingFoerstegangVarselMedRevarsel(bestillingTo, varselInfoTo, kontaktregisterTo))
				.thenReturn(newVarselbestilling);

		when(domainMapper.mapReVarsel(KANAL_CODE, bestillingTo, varselInfoTo, kontaktregisterTo))
				.thenReturn(varsel);
		when(varselutsendingToMapper
				.mapVarsels(eq(existingVarselbestilling), eq(UTLOP_TIDSPUNKT), eq(Sets.newHashSet(varsel))))
				.thenReturn(Lists.newArrayList(varselutsendingTo));
	}

	@Test
	public void shouldBestillRevarselNotLastRevarsel() throws Exception {
		createBestillingTo(VARSELBESTILLING_ID, true);
		bestillVarselService.bestillVarsel(bestillingTo);

		assertThat(existingVarselbestilling.getAntallRevarslinger(), is(ANTALL_REVARSLINGER - 1));
		assertThat(existingVarselbestilling.getNesteVarslingDato(), is(LocalDate.now().plusDays(REVARSLING_INTERVALL)));
		verify(varselbestillingRepoMock).saveAndFlush(existingVarselbestilling);
		verify(varselutsendingProducer).produce(varselutsendingTo);
	}

	@Test
	public void shouldBestillRevarselLastRevarsel() throws Exception {
		createBestillingTo(VARSELBESTILLING_ID, true);
		existingVarselbestilling.setAntallRevarslinger(1);
		bestillVarselService.bestillVarsel(bestillingTo);

		assertThat(existingVarselbestilling.getAntallRevarslinger(), nullValue());
		assertThat(existingVarselbestilling.getNesteVarslingDato(), nullValue());
		verify(varselbestillingRepoMock).saveAndFlush(existingVarselbestilling);
		verify(varselutsendingProducer).produce(varselutsendingTo);
	}

	@Test
	public void shouldBestillFoerstegangsvarsel() throws Exception {
		when(varselutsendingToMapper
				.mapVarsels(eq(newVarselbestilling), eq(UTLOP_TIDSPUNKT), eq(Sets.newHashSet(varsel))))
				.thenReturn(Lists.newArrayList(varselutsendingTo));

		createBestillingTo(NEW_BESTILLING_ID, false);
		bestillVarselService.bestillVarsel(bestillingTo);

		verify(varselbestillingRepoMock).saveAndFlush(newVarselbestilling);
		verify(varselutsendingProducer).produce(varselutsendingTo);
	}

	@Test
	public void shouldThrowFunctionalForRevarsel_VarselIkkefunnet() throws Exception {
		expectedException.expect(VarselbestillingNotExistException.class);

		createBestillingTo("unknown", true);
		bestillVarselService.bestillVarsel(bestillingTo);
	}

	@Test
	public void shouldThrowFunctionalForVarsel_VarselEksistererAllerede() throws Exception {
		expectedException.expect(VarselbestillingAlreadyExistException.class);

		createBestillingTo(VARSELBESTILLING_ID, false);
		bestillVarselService.bestillVarsel(bestillingTo);
	}

	@Test
	public void shouldThrowFunctionalForVarsel_VarselEksistererAllerede_antallRevarselNull() throws Exception {
		expectedException.expectMessage("already sendt, antallRevarslinger=null, nesteVarslingDato=" + NESTE_VARSLING_DATO);
		expectedException.expect(VarselbestillingAlreadyExistException.class);

		existingVarselbestilling.setAntallRevarslinger(null);
		createBestillingTo(VARSELBESTILLING_ID, true);
		bestillVarselService.bestillVarsel(bestillingTo);
	}

	@Test
	public void shouldThrowFunctionalForVarsel_VarselEksistererAllerede_antallRevarselZero() throws Exception {
		expectedException.expectMessage("already sendt, antallRevarslinger=0, nesteVarslingDato=" + NESTE_VARSLING_DATO);
		expectedException.expect(VarselbestillingAlreadyExistException.class);

		existingVarselbestilling.setAntallRevarslinger(0);
		createBestillingTo(VARSELBESTILLING_ID, true);
		bestillVarselService.bestillVarsel(bestillingTo);
	}

	@Test
	public void shouldThrowFunctionalForVarsel_VarselEksistererAllerede_nesteRevarselNull() throws Exception {
		expectedException.expectMessage("already sendt, antallRevarslinger=2, nesteVarslingDato=null");
		expectedException.expect(VarselbestillingAlreadyExistException.class);

		existingVarselbestilling.setNesteVarslingDato(null);
		createBestillingTo(VARSELBESTILLING_ID, true);
		bestillVarselService.bestillVarsel(bestillingTo);
	}

	@Test
	public void shouldThrowFunctionalForVarsel_VarselEksistererAllerede_nesteRevarselInFuture() throws Exception {
		LocalDate futureTime = LocalDate.now().plusDays(1);
		expectedException.expectMessage("already sendt, antallRevarslinger=2, nesteVarslingDato=" + futureTime);
		expectedException.expect(VarselbestillingAlreadyExistException.class);

		existingVarselbestilling.setNesteVarslingDato(futureTime);
		createBestillingTo(VARSELBESTILLING_ID, true);
		bestillVarselService.bestillVarsel(bestillingTo);
	}

	@Test
	public void shouldThrowFunctionalForVarselbestilling_VarselbestillingUtloept() throws Exception {
		LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
		expectedException.expectMessage("Varselbestilling with varselbestillingId=" + VARSELBESTILLING_ID + " has utloepstidspunkt=" + pastTime);
		expectedException.expect(VarselbestillingUtloeptException.class);

		BestillVarselTo bestillingTo = createBestillingTo(VARSELBESTILLING_ID, true);
		bestillingTo.setUtloepstidspunkt(pastTime);
		bestillVarselService.bestillVarsel(this.bestillingTo);
	}

	private BestillVarselTo createBestillingTo(String bestillingId, boolean revarsling) {
		bestillingTo.setVarselBestillingId(bestillingId);
		bestillingTo.setRevarsling(revarsling);
		bestillingTo.setMottaker(aktoerTo);
		bestillingTo.setUtloepstidspunkt(UTLOP_TIDSPUNKT);
		bestillingTo.setVarseltypeId(VARSELTYPE_ID);
		return bestillingTo;
	}
}