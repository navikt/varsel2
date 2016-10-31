package no.nav.varsel.service;

import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.EPOST;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.OVERSTYRT_PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.TLF;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.VarselbestillingUtloeptException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
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

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, OVERSTYRT_PREFERERT_KANAL)).thenReturn(TestdataUtil.OVERSTYRT_PREFERERT_KANAL);

		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling))).thenReturn(varselutsendingTos);
	}

	@Test
	public void shouldBestillServicemelding() throws Exception {
		bestilling.setAktoerId(AKTOR_ID);
		servicemeldingService.bestillServicemelding(bestilling);

		assertOK();
	}

	@Test(expected = ArithmeticException.class)
	public void shouldThrowTekniskForTekniskFeilDkif() throws Exception {
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(TEKNISK));
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(TEKNISK)).thenThrow(new ArithmeticException(TEKNISK));
		servicemeldingService.bestillServicemelding(bestilling);
	}

	@Test
	public void throwsInaktivVarselmalExceptionForInaktivVarselmal() {
		expectedException.expectMessage(
				"Det er ikke mulig å bestille servicemelding for mottaker med mottakerId=" +
						FNR + " og bestillingId=");
		expectedException.expectMessage(" med inaktiv varselmal med varseltypeId=" + TestdataUtil.VARSELTYPE_ID + ".");

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
		} catch (VarselInaktivVarselmalException ive) {
			verify(varselbestillingRepo, never()).saveAndFlush(anyObject());
		}
	}

	@Test
	public void preferertKanalIsOverreidenWhenTestVarsel() {
		bestilling.setTestvarsel(true);
		varselInfoTo.setInaktiv(false);
		servicemeldingService.bestillServicemelding(bestilling);
		verify(varselKanalDecider, never()).decideKanaler(kontaktregisterTo, PREFERERT_KANAL);
		verify(varselKanalDecider, times(1)).decideKanaler(kontaktregisterTo, OVERSTYRT_PREFERERT_KANAL);
	}

	@Test
	public void preferertKanallisteNotOverridenForNormalVarsler() {
		bestilling.setTestvarsel(false);
		varselInfoTo.setInaktiv(false);
		servicemeldingService.bestillServicemelding(bestilling);
		verify(varselKanalDecider, times(1)).decideKanaler(kontaktregisterTo, PREFERERT_KANAL);
		verify(varselKanalDecider, never()).decideKanaler(kontaktregisterTo, OVERSTYRT_PREFERERT_KANAL);
	}

	@Test
	public void shouldNotCallDkiWhenEpostAndTlfIsSet() throws Exception {
		createKontaktInfoBestilling();
		servicemeldingService.bestillServicemelding(bestilling);

		verify(digitalKontaktinformasjonConsumer, never()).hentDigitalKontaktinformasjon(anyString());
	}

	@Test
	public void shouldNotSendDittNavToDecider() {
		createKontaktInfoBestilling();
		varselInfoTo.setPreferertKanal(Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS, KanalCode.DITT_NAV));

		servicemeldingService.bestillServicemelding(bestilling);

		verify(varselKanalDecider).decideKanaler(any(KontaktregisterTo.class), eq(Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS)));
	}

	@Test
	public void shouldSendTelefonnummerAndEpostToDecider() {
		createKontaktInfoBestilling();
		servicemeldingService.bestillServicemelding(bestilling);

		ArgumentCaptor<KontaktregisterTo> captor = ArgumentCaptor.forClass(KontaktregisterTo.class);
		verify(varselKanalDecider).decideKanaler(captor.capture(), eq(PREFERERT_KANAL));
		KontaktregisterTo value = captor.getValue();

		assertThat(value.getEpostadresse(), is(EPOST));
		assertThat(value.getMobiltelefonnummer(), is(TLF));
	}

	@Test
	public void shouldThrowFunctionalForVarselbestilling_VarselbestillingUtloept() throws Exception {
		LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
		expectedException.expectMessage("Varselbestilling has utloepstidspunkt=" + pastTime);
		expectedException.expect(VarselbestillingUtloeptException.class);

		bestilling.setUtloepstidspunkt(pastTime);
		servicemeldingService.bestillServicemelding(bestilling);
	}

	private void createKontaktInfoBestilling() {
		bestilling.setVarseltypeId(VARSELTYPE_ID);
		bestilling.setEpost(EPOST);
		bestilling.setMobiltelefonnummer(TLF);
		bestilling.setOrgNr(TestdataUtil.ORG_NR);
	}

	private void assertOK() {
		assertThat(UUID.fromString(bestilling.getVarselBestillingId()).toString(), is(bestilling.getVarselBestillingId()));
		verify(varselbestillingRepo).saveAndFlush(varselbestilling);

		verify(varselutsendingProducer).produce(varselutsendingTos.get(0));
		verify(varselutsendingProducer).produce(varselutsendingTos.get(1));
		verifyNoMoreInteractions(varselutsendingProducer, varselbestillingRepo);
	}
}