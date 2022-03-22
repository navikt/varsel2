package no.nav.varsel.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.VarselBestillingDomainMapper;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.EPOST;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.OVERSTYRT_PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.TLF;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


/**
 * Unit test for {@link ServicemeldingService}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@ExtendWith({MockitoExtension.class})
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

	@BeforeEach
	public void setUp() throws Exception {
		// reset
		bestilling.setVarselBestillingId(null);
		bestilling.setPersonIdent(null);
		bestilling.setAktoerId(null);
		bestilling.setVarseltypeId(VARSELTYPE_ID);

		varselInfoTo.setPreferertKanal(PREFERERT_KANAL);
/*
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, OVERSTYRT_PREFERERT_KANAL)).thenReturn(TestdataUtil.OVERSTYRT_PREFERERT_KANAL);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling))).thenReturn(varselutsendingTos);
*/
	}

	@Test
	public void shouldBestillServicemelding() throws Exception {
		bestilling.setAktoerId(AKTOR_ID);
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling))).thenReturn(varselutsendingTos);
		servicemeldingService.bestillServicemelding(bestilling);

		assertOK();
	}

	@Test
	public void shouldThrowTekniskForTekniskFeilDkif() throws Exception {
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(TEKNISK));
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(TEKNISK)).thenThrow(new ArithmeticException(TEKNISK));

		Executable executable = () -> servicemeldingService.bestillServicemelding(bestilling);
		Assertions.assertThrows(ArithmeticException.class, executable);
	}

	@Test
	public void throwsInaktivVarselmalExceptionForInaktivVarselmal() {
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		bestilling.setTestvarsel(false);
		varselInfoTo.setInaktiv(true);
		varselInfoTo.setVarseltypeId(VARSELTYPE_ID);

		Executable executable = () -> servicemeldingService.bestillServicemelding(bestilling);

		Exception exception = Assertions.assertThrows(VarselInaktivVarselmalException.class, executable);
		assertTrue(exception.getMessage().contains("Det er ikke mulig å bestille servicemelding for mottaker med mottakerId=" +
				FNR + " og bestillingId="));
		assertTrue(exception.getMessage().contains(" med inaktiv varselmal med varseltypeId=" + TestdataUtil.VARSELTYPE_ID + "."));
	}

	@Test
	public void doesNotStoreVarselbestillingWhenInaktivVarselmal() {
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
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
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		bestilling.setTestvarsel(true);
		varselInfoTo.setInaktiv(false);
		servicemeldingService.bestillServicemelding(bestilling);
		verify(varselKanalDecider, never()).decideKanaler(kontaktregisterTo, PREFERERT_KANAL);
		verify(varselKanalDecider, times(1)).decideKanaler(kontaktregisterTo, OVERSTYRT_PREFERERT_KANAL);
	}

	@Test
	public void preferertKanallisteNotOverridenForNormalVarsler() {
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		bestilling.setTestvarsel(false);
		varselInfoTo.setInaktiv(false);
		servicemeldingService.bestillServicemelding(bestilling);
		verify(varselKanalDecider, times(1)).decideKanaler(kontaktregisterTo, PREFERERT_KANAL);
		verify(varselKanalDecider, never()).decideKanaler(kontaktregisterTo, OVERSTYRT_PREFERERT_KANAL);
	}

	@Test
	public void shouldNotCallDkiWhenEpostAndTlfIsSet() throws Exception {
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		createKontaktInfoBestilling();
		servicemeldingService.bestillServicemelding(bestilling);

		verify(digitalKontaktinformasjonConsumer, never()).hentDigitalKontaktinformasjon(anyString());
	}

	@Test
	public void shouldNotSendDittNavToDecider() {
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		createKontaktInfoBestilling();
		varselInfoTo.setPreferertKanal(Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS, KanalCode.DITT_NAV));

		servicemeldingService.bestillServicemelding(bestilling);

		verify(varselKanalDecider).decideKanaler(any(KontaktregisterTo.class), eq(Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS)));
	}

	@Test
	public void shouldSendTelefonnummerAndEpostToDecider() {
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
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

		bestilling.setUtloepstidspunkt(pastTime);
		Executable executable = () -> servicemeldingService.bestillServicemelding(bestilling);
		Exception exception = Assertions.assertThrows(VarselbestillingUtloeptException.class, executable);
		assertTrue(exception.getMessage().contains("Varselbestilling has utloepstidspunkt=" + pastTime));
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