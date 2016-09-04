package no.nav.varsel.service;

import static no.nav.varsel.domain.to.AktoerTo.newAktoerId;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.VarselInaktivVarselmalException;
import no.nav.varsel.service.to.AktoerBestillingTo;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Unit test for {@link ServicemeldingService}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServicemeldingServiceTest {

	private static final String TEKNISK = "teknisk";

	@Mock
	private AktoerService aktoerService;
	@Mock
	private VarselInfoConsumer varselInfoConsumer;
	@Mock
	private HentDigitalKontaktinformasjonConsumer digitalKontaktinformasjonConsumer;

	@Mock
	private VarselutsendingProducer varselutsendingProducer;
	@Mock
	private VarselutsendingToMapper varselutsendingToMapper;

	@Mock
	private VarselBestillingDomainMapper domainMapper;
	@Mock
	private VarselKanalDecider varselKanalDecider;
	@Mock
	private VarselbestillingRepo varselbestillingRepo;

	@InjectMocks
	private ServicemeldingService servicemeldingService;

	private ArrayList<VarselutsendingTo> varselutsendingTos = Lists.newArrayList(new VarselutsendingTo(), new VarselutsendingTo());
	private Varselbestilling varselbestilling = new Varselbestilling();
	private BestillVarselTo bestilling = new BestillVarselTo();
	private KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();
	private AktoerTo aktoerTo = newAktoerId(TestdataUtil.AKTOR_ID);
	private VarselInfoTo varselInfoTo = new VarselInfoTo();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();


	@Before
	public void setUp() throws Exception {
		// reset
		bestilling.setVarselBestillingId(null);
		bestilling.setPersonIdent(null);
		bestilling.setAktoerId(null);
		bestilling.setVarseltypeId(VARSELTYPE_ID);

		varselInfoTo.setPreferertKanal(PREFERERT_KANAL);

		when(aktoerService.completeAktoerPersonIdent(bestilling)).thenAnswer(
				invocation -> {
					if (invocation.getArgumentAt(0, AktoerBestillingTo.class).getPersonIdent() == null)
						bestilling.setMottaker(newPersonIdent(FNR));
					return aktoerTo;
				}
		);
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjonAndDecideKanal(FNR, PREFERERT_KANAL)).thenReturn(kontaktregisterTo);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling), eq(aktoerTo))).thenReturn(varselutsendingTos);
	}

	@Test
	public void shouldBestillServicemelding() throws Exception {
		bestilling.setAktoerId(AKTOR_ID);
		servicemeldingService.bestillServicemelding(bestilling);

		assertOK();
	}

	@Test(expected = ArithmeticException.class)
	public void shouldThrowTekniskForTekniskFeilDkif() throws Exception {
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjonAndDecideKanal(TEKNISK, PREFERERT_KANAL)).thenThrow(new ArithmeticException(TEKNISK));
		bestilling.setPersonIdent(TEKNISK);
		servicemeldingService.bestillServicemelding(bestilling);
	}

	@Test
	public void throwsInaktivVarselmalExceptionForInaktivVarselmal() {
		expectedException.expectMessage(
				"Mottaker med id " +
						FNR +
						" bruker inaktiv varselmal med id " +
						TestdataUtil.VARSELTYPE_ID + ".");

		expectedException.expect(VarselInaktivVarselmalException.class);
		bestilling.setTestvarsel(false);
		varselInfoTo.setInaktiv(true);
		varselInfoTo.setVarseltypeId(VARSELTYPE_ID);
		servicemeldingService.bestillServicemelding(bestilling);
	}

	@Test
	public void doesNotStoreVarselbestillingWhenInaktivVarselmal() {
		bestilling.setTestvarsel(false);
		varselInfoTo.setInaktiv(true);
		varselInfoTo.setVarseltypeId(VARSELTYPE_ID);
		try {
			servicemeldingService.bestillServicemelding(bestilling);
			fail();
		} catch(VarselInaktivVarselmalException ive) {
			verify(varselbestillingRepo,times(0)).saveAndFlush(anyObject());
		}
	}

	private void assertOK() {
		assertThat(UUID.fromString(bestilling.getVarselBestillingId()).toString(), is(bestilling.getVarselBestillingId()));
		verify(varselbestillingRepo).saveAndFlush(varselbestilling);

		verify(varselutsendingProducer).produce(varselutsendingTos.get(0));
		verify(varselutsendingProducer).produce(varselutsendingTos.get(1));
		verifyNoMoreInteractions(varselutsendingProducer, varselbestillingRepo);
	}
}