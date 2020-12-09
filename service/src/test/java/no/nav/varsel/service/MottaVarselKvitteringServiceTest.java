package no.nav.varsel.service;

import static no.nav.varsel.service.MottaVarselKvitteringService.MAX_LENGTH_FEILMELDING;
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
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringStatusTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringToTest;
import org.apache.commons.lang3.StringUtils;
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
		expectedException.expectMessage("Varsel with varselId=" + to.getVarselId() +
				" has invalid statusCode=" + StatusCode.OPPRETTET);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);
	}

	@Test
	public void shouldNotThrowIfVarselHasStatusFERDIGBEHANDLET() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();

		Varsel varsel = createVarsel(to.getVarselId());
		varsel.setStatus(StatusCode.FERDIGBEHANDLET);

		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), is(StatusCode.FERDIGBEHANDLET));
		assertThat(varsel.getDistribusjonTidspunkt(), equalTo(to.getUtsendingstidspunkt()));
		assertThat(varsel.getKvitteringTidspunkt(), aboutNow());
	}

	@Test
	public void shouldUpdateVarselWhenStatusOK() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();

		Varsel varsel = createVarsel(to.getVarselId());
		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), is(StatusCode.FERDIGBEHANDLET));
		assertThat(varsel.getDistribusjonTidspunkt(), equalTo(to.getUtsendingstidspunkt()));
		assertThat(varsel.getKvitteringTidspunkt(), aboutNow());
	}

	@Test
	public void shouldUpdateVarselWhenStatusError() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();
		to.setStatus(MottaVarselKvitteringStatusTo.ERROR);

		Varsel varsel = createVarsel(to.getVarselId());
		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), equalTo(StatusCode.FEILET));
		assertThat(varsel.getFeilbeskrivelse(), equalTo(to.getFeilmelding()));
		assertThat(varsel.getKvitteringTidspunkt(), aboutNow());
	}

	@Test
	public void shouldUpdateVarselWhenStatusExpired() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();
		to.setStatus(MottaVarselKvitteringStatusTo.EXPIRED);

		Varsel varsel = createVarsel(to.getVarselId());
		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), equalTo(StatusCode.FEILET));
		assertThat(varsel.getFeilbeskrivelse(), equalTo(to.getFeilmelding()));
		assertThat(varsel.getKvitteringTidspunkt(), aboutNow());
	}

	@Test
	public void shouldCropFeilmeldingWhenTooLong() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();
		to.setStatus(MottaVarselKvitteringStatusTo.ERROR);
		to.setFeilmelding(StringUtils.repeat("a", MAX_LENGTH_FEILMELDING + 1));

		Varsel varsel = createVarsel(to.getVarselId());
		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), equalTo(StatusCode.FEILET));
		assertThat(varsel.getFeilbeskrivelse().length(), equalTo(MAX_LENGTH_FEILMELDING));
		assertThat(varsel.getFeilbeskrivelse().endsWith("..."), is(true));

	}

	@Test
	public void shouldOnlyCropFeilmeldingWhenTooLong() throws Exception {
		MottaVarselKvitteringTo to = MottaVarselKvitteringToTest.createTo();
		to.setStatus(MottaVarselKvitteringStatusTo.ERROR);
		to.setFeilmelding(StringUtils.repeat("a", MAX_LENGTH_FEILMELDING));

		Varsel varsel = createVarsel(to.getVarselId());
		when(varselRepo.findByVarselId(to.getVarselId())).thenReturn(varsel);

		mottaVarselKvitteringService.behandleKvitteringsmelding(to);

		assertThat(varsel.getStatus(), equalTo(StatusCode.FEILET));
		assertThat(varsel.getFeilbeskrivelse().length(), equalTo(MAX_LENGTH_FEILMELDING));
		assertThat(varsel.getFeilbeskrivelse().endsWith("..."), is(false));
	}

	@Test
	public void shouldCensorPersonalData() throws Exception {
		String uncensoredTlf = "The phone number has an invalid country code: +004712345678";
		String uncensoredMailAndFnr = "Address: ola.normann.ÅLAND_123@epøst.no, User: 01017012345";

		String censoredTlf = mottaVarselKvitteringService.sensurerPersonligData(uncensoredTlf);
		String censoredMailAndFnr = mottaVarselKvitteringService.sensurerPersonligData(uncensoredMailAndFnr);

		assertThat(censoredTlf, equalTo("The phone number has an invalid country code: +0047****"));
		assertThat(censoredMailAndFnr, equalTo("Address: o***@e***.no, User: ****"));
	}

	private Varsel createVarsel(String varselId) {
		Varsel varsel = new Varsel();
		varsel.setVarselId(varselId);
		varsel.setStatus(StatusCode.SENDT);
		return varsel;
	}
}