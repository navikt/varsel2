package no.nav.varsel.service;

import static no.nav.varsel.test.TestUtils.aboutNow;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.service.support.exception.InvalidVarselStatusException;
import no.nav.varsel.service.support.exception.VarselNotExistException;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringToTest;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringStatusTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link MottaVarselKvitteringService}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class MottaVarselKvitteringServiceTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private VarselRepo varselRepo;

	@InjectMocks
	private MottaVarselKvitteringService mottaVarselKvitteringService;

	@Test
	public void shouldThrowExceptionIfVarselIdNotExist() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();

		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(null);

		expectedException.expect(VarselNotExistException.class);
		expectedException.expectMessage("Varsel with varselId=" + to.getVarselId() + " does not exist");

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);
	}

	@Test
	public void shouldThrowExceptionIfVarselNotHasStatusSendt() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();

		Varsel varsel = createVarsel(to.getVarselId());
		varsel.setStatus(StatusCode.OPPRETTET);

		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		expectedException.expect(InvalidVarselStatusException.class);
		expectedException.expectMessage("Varsel with varselId=" + to.getVarselId() + " has invalid statusCode=" + StatusCode.OPPRETTET);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);
	}

	@Test
	public void shouldUpdateVarselWhenStatusPlukket() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();

		Varsel varsel = createVarsel(to.getVarselId());
		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), is(StatusCode.FERDIGBEHANDLET));
		assertThat(varsel.getDistribusjonTidspunkt(), equalTo(to.getUtsendingstidspunkt()));
		assertThat(varsel.getKvitteringTidspunkt(), aboutNow());
	}

	@Test
	public void shouldUpdateVarselWhenStatusFeilet() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();
		to.setStatus(MottaVarselKvitteringStatusTo.FEILET);

		Varsel varsel = createVarsel(to.getVarselId());
		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), equalTo(StatusCode.FEILET));
		assertThat(varsel.getFeilbeskrivelse(), equalTo(to.getFeilmelding()));
		assertThat(varsel.getKvitteringTidspunkt(), aboutNow());
	}

	private Varsel createVarsel(String varselId) {
		Varsel varsel = new Varsel();
		varsel.setVarselId(varselId);
		varsel.setStatus(StatusCode.SENDT);
		return varsel;
	}
}