package no.nav.varsel.service;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;
import no.nav.varsel.service.to.AktoerBestillingTo;
import no.nav.varsel.wsconsumer.pdl.PdlIdentConsumer;
import no.nav.varsel.wsconsumer.pdl.support.PdlFunctionalException;
import no.nav.varsel.wsconsumer.pdl.support.PersonIkkeFunnetException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AktoerServiceTest {

	@Mock
	private PdlIdentConsumer pdlIdentConsumer;

	@InjectMocks
	private AktoerService aktoerService;

	@Test
	public void shouldHentAktoerIdForFolkeregisterident() {
		when(pdlIdentConsumer.hentAktoerId(eq(FNR))).thenReturn(AKTOR_ID);
		AktoerBestillingTo aktoerBestillingTo = createAktoerBestillingPersonIdent();

		AktoerTo missingAktoer = aktoerService.findMissingAktoer(aktoerBestillingTo);

		assertThat(missingAktoer.getIdent(), is(AKTOR_ID));
		assertThat(missingAktoer.getMottakerType(), is(MottakerType.AKTOER));
	}

	@Test
	public void shouldHentFolkeregisteridentForAktoerId() {
		when(pdlIdentConsumer.hentFolkeregisterIdent(eq(AKTOR_ID))).thenReturn(FNR);
		AktoerBestillingTo aktoerBestillingTo = createAktoerBestillingAktoerId();

		AktoerTo missingAktoer = aktoerService.findMissingAktoer(aktoerBestillingTo);

		assertThat(missingAktoer.getIdent(), is(FNR));
		assertThat(missingAktoer.getMottakerType(), is(MottakerType.PERSON));
	}

	@Test(expected = PersonIkkeFunnetException.class)
	public void shouldThrowAktoerIkkeFunnetExceptionForFunksjonellFeilAktoer() {
		when(pdlIdentConsumer.hentFolkeregisterIdent(eq(AKTOR_ID)))
				.thenThrow(new PersonIkkeFunnetException(""));

		AktoerBestillingTo aktoerBestillingTo = createAktoerBestillingAktoerId();
		aktoerService.findMissingAktoer(aktoerBestillingTo);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenArithmeticExceptionIsThrown() {
		when(pdlIdentConsumer.hentAktoerId(FNR)).thenThrow(new RuntimeException("teknisk feil"));

		AktoerBestillingTo aktoerBestillingTo = createAktoerBestillingPersonIdent();
		aktoerService.findMissingAktoer(aktoerBestillingTo);
	}

	@Test(expected = PdlFunctionalException.class)
	public void shouldThrowExceptionIfPdlFunctionalExceptionIsThrown() {
		when(pdlIdentConsumer.hentFolkeregisterIdent(eq(AKTOR_ID))).thenThrow(PdlFunctionalException.class);

		AktoerBestillingTo aktoerBestillingTo = createAktoerBestillingAktoerId();
		aktoerService.findMissingAktoer(aktoerBestillingTo);
	}

	private AktoerBestillingTo createAktoerBestillingPersonIdent() {
		AktoerBestillingTo aktoerBestillingTo = new AktoerBestillingTo();
		aktoerBestillingTo.setPersonIdent(FNR);
		return aktoerBestillingTo;
	}

	private AktoerBestillingTo createAktoerBestillingAktoerId() {
		AktoerBestillingTo aktoerBestillingTo = new AktoerBestillingTo();
		aktoerBestillingTo.setAktoerId(AKTOR_ID);
		return aktoerBestillingTo;
	}

}