package no.nav.varsel.service;

import static no.nav.varsel.domain.to.AktoerTo.newAktoerId;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.feil.PersonIkkeFunnet;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.service.to.AktoerBestillingTo;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.aktoer.support.AktoerIkkeFunnetException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link AktoerService}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class AktoerServiceTest {

	private static final String FUNKSJONELL = "funksjonell";
	private static final String TEKNISK = "teknisk";

	private ArithmeticException teknisk = new ArithmeticException(TEKNISK);

	@Mock
	private AktoerConsumer aktoerConsumer;
	@InjectMocks
	private AktoerService aktoerService;

	private AktoerTo origAktoerTo = newAktoerId(TestdataUtil.AKTOR_ID);
	private AktoerTo fetchedAktoerTo = newPersonIdent(FNR);
	private AktoerBestillingTo aktoerBestillingTo;

	@Before
	public void setUp() throws Exception {
		when(aktoerConsumer.hentIdent(eq(origAktoerTo))).thenReturn(fetchedAktoerTo);

		aktoerBestillingTo = new AktoerBestillingTo();
		aktoerBestillingTo.setMottaker(origAktoerTo);
	}

	@Test
	public void shouldHentIdent() throws Exception {
		AktoerTo missingAktoer = aktoerService.findMissingAktoer(aktoerBestillingTo);
		assertThat(missingAktoer, is(fetchedAktoerTo));
	}

	@Test(expected = AktoerIkkeFunnetException.class)
	public void shouldThrowAktoerIkkeFunnetExceptionForFunksjonellFeilAktoer() throws Exception {
		when(aktoerConsumer.hentIdent(eq(newAktoerId(FUNKSJONELL))))
				.thenThrow(new HentAktoerIdForIdentPersonIkkeFunnet("", new PersonIkkeFunnet()));

		aktoerBestillingTo.setAktoerId(FUNKSJONELL);
		aktoerService.findMissingAktoer(aktoerBestillingTo);
	}

	@Test(expected = ArithmeticException.class)
	public void shouldThrowExceptionIfTekniskForTekniskFeilAktoer() throws Exception {
		when(aktoerConsumer.hentIdent(eq(newAktoerId(TEKNISK)))).thenThrow(teknisk);

		aktoerBestillingTo.setAktoerId(TEKNISK);
		aktoerService.findMissingAktoer(aktoerBestillingTo);
	}

}