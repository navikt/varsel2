package no.nav.varsel.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.ServicemeldingUtil;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.support.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.BrukernotifikasjonMapper;
import no.nav.varsel.service.tvarsel001.support.NotifikasjonMapper;
import no.nav.varsel.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.tvarsel006.support.NotifikasjonMedKontaktinfoMapper;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import no.nav.varsel.tvarsel001.NotifikasjonPublisher;
import no.nav.varsel.tvarsel006.NotifikasjonMedKontaktinfoPublisher;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
	private VarselutsendingToMapper varselutsendingToMapper;

	@Mock
	private VarselBestillingDomainMapper domainMapper;
	@Mock
	private VarselKanalDecider varselKanalDecider;
	@Mock
	private VarselbestillingRepo varselbestillingRepo;

	@Mock
	private NotifikasjonMedKontaktinfoPublisher notifikasjonMedKontaktinfoPublisher;

	@Mock
	private NotifikasjonMedKontaktinfoMapper notifikasjonMedkontaktInfoMapper;

	@Mock
	private NotifikasjonPublisher notifikasjonPublisher;

	@Mock
	private NotifikasjonMapper notifikasjonMapper;

	@Mock
	private BrukernotifikasjonBeskjedPublisher brukernotifikasjonBeskjedPublisher;

	@Mock
	private BrukernotifikasjonMapper brukernotifikasjonMapper;

	@InjectMocks
	private ServicemeldingService servicemeldingService;

	private final ArrayList<VarselutsendingTo> varselutsendingTos = Lists.newArrayList(new VarselutsendingTo(), new VarselutsendingTo());
	private final Varselbestilling varselbestilling = new Varselbestilling();
	private final BestillVarselTo bestilling = new BestillVarselTo();
	private final KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();
	private final VarselInfoTo varselInfoTo = new VarselInfoTo();
	private final Doknotifikasjon doknotifikasjon = new Doknotifikasjon();
	private final BeskjedInput beskjedInput = new BeskjedInput();
	private final NokkelInput nokkelInput = new NokkelInput();
	private final NotifikasjonMedkontaktInfo notifikasjonMedkontaktInfo = new NotifikasjonMedkontaktInfo();

	@BeforeEach
	public void setUp() {
		// reset
		bestilling.setVarselBestillingId(null);
		bestilling.setPersonIdent(null);
		bestilling.setAktoerId(null);
		bestilling.setVarseltypeId(VARSELTYPE_ID);

		varselInfoTo.setPreferertKanal(PREFERERT_KANAL);
	}

	@Test
	public void shouldBestillServicemelding() {
		bestilling.setAktoerId(AKTOR_ID);
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling))).thenReturn(varselutsendingTos);

		when(notifikasjonMapper.mapNotifikasjon(varselbestilling, varselutsendingTos.get(0), varselInfoTo)).thenReturn(doknotifikasjon);
		when(notifikasjonMapper.mapNotifikasjon(varselbestilling, varselutsendingTos.get(1), varselInfoTo)).thenReturn(doknotifikasjon);

		servicemeldingService.bestillServicemelding(bestilling);

		assertOK();
	}

	@Test
	public void shouldBestillServicemeldingMedBrukernotifikasjon() {
		bestilling.setAktoerId(AKTOR_ID);
		varselInfoTo.setMaler(ServicemeldingUtil.createMaler());
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);

		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL_MED_DITT_NAV);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling))).thenReturn(varselutsendingTos);

		when(notifikasjonMapper.mapNotifikasjon(varselbestilling, varselutsendingTos.get(0), varselInfoTo)).thenReturn(doknotifikasjon);
		when(notifikasjonMapper.mapNotifikasjon(varselbestilling, varselutsendingTos.get(1), varselInfoTo)).thenReturn(doknotifikasjon);

		when(brukernotifikasjonMapper.mapBeskjed(varselInfoTo, varselutsendingTos.get(0))).thenReturn(beskjedInput);
		when(brukernotifikasjonMapper.mapBeskjed(varselInfoTo, varselutsendingTos.get(1))).thenReturn(beskjedInput);
		when(brukernotifikasjonMapper.mapNokkel(varselbestilling)).thenReturn(nokkelInput);

		servicemeldingService.bestillServicemelding(bestilling);

		assertOkMedBrukernotifikasjon();
	}

	@Test
	public void shouldBestillServicemeldingMedKontaktinfo() {
		bestilling.setAktoerId(AKTOR_ID);
		bestilling.setEpost("test@epost.no");
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(varselKanalDecider.decideKanaler(any(KontaktregisterTo.class), eq(PREFERERT_KANAL))).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(eq(bestilling), eq(varselInfoTo), any(KontaktregisterTo.class))).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling))).thenReturn(varselutsendingTos);

		when(notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(bestilling, varselbestilling, varselutsendingTos.get(0), varselInfoTo)).thenReturn(notifikasjonMedkontaktInfo);
		when(notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(bestilling, varselbestilling, varselutsendingTos.get(1), varselInfoTo)).thenReturn(notifikasjonMedkontaktInfo);

		servicemeldingService.bestillServicemelding(bestilling);

		assertOkMedKontaktinfo();
	}

	@Test
	public void shouldThrowTekniskForTekniskFeilDkif() {
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
			verify(varselbestillingRepo, never()).saveAndFlush(any());
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
	public void shouldNotCallDkiWhenEpostAndTlfIsSet() {
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		setKontaktInfo();
		servicemeldingService.bestillServicemelding(bestilling);

		verify(digitalKontaktinformasjonConsumer, never()).hentDigitalKontaktinformasjon(anyString());
	}

	@Test
	public void shouldNotSendDittNavToDecider() {
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		setKontaktInfo();
		varselInfoTo.setPreferertKanal(Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS, KanalCode.DITT_NAV));

		servicemeldingService.bestillServicemelding(bestilling);

		verify(varselKanalDecider).decideKanaler(any(KontaktregisterTo.class), eq(Sets.newHashSet(KanalCode.EPOST, KanalCode.SMS)));
	}

	@Test
	public void shouldSendTelefonnummerAndEpostToDecider() {
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		setKontaktInfo();

		servicemeldingService.bestillServicemelding(bestilling);

		ArgumentCaptor<KontaktregisterTo> captor = ArgumentCaptor.forClass(KontaktregisterTo.class);
		verify(varselKanalDecider).decideKanaler(captor.capture(), eq(PREFERERT_KANAL));
		KontaktregisterTo value = captor.getValue();

		assertThat(value.getEpostadresse(), is(EPOST));
		assertThat(value.getMobiltelefonnummer(), is(TLF));
	}

	@Test
	public void shouldThrowFunctionalForVarselbestilling_VarselbestillingUtloept() {
		LocalDateTime pastTime = LocalDateTime.now().minusDays(1);

		bestilling.setUtloepstidspunkt(pastTime);
		Executable executable = () -> servicemeldingService.bestillServicemelding(bestilling);
		Exception exception = Assertions.assertThrows(VarselbestillingUtloeptException.class, executable);
		assertTrue(exception.getMessage().contains("Varselbestilling has utloepstidspunkt=" + pastTime));
	}

	private void setKontaktInfo() {
		bestilling.setVarseltypeId(VARSELTYPE_ID);
		bestilling.setEpost(EPOST);
		bestilling.setMobiltelefonnummer(TLF);
		bestilling.setOrgNr(TestdataUtil.ORG_NR);
	}

	private void assertOK() {
		assertThat(UUID.fromString(bestilling.getVarselBestillingId()).toString(), is(bestilling.getVarselBestillingId()));
		verify(varselbestillingRepo).saveAndFlush(varselbestilling);

		verify(notifikasjonPublisher, times(2)).sendNotifikasjon(any(Doknotifikasjon.class));
		verify(brukernotifikasjonBeskjedPublisher, never()).sendNotifikasjon(any(BeskjedInput.class), any(NokkelInput.class));

		verifyNoMoreInteractions(varselbestillingRepo);
	}

	private void assertOkMedBrukernotifikasjon() {
		assertThat(UUID.fromString(bestilling.getVarselBestillingId()).toString(), is(bestilling.getVarselBestillingId()));
		verify(varselbestillingRepo).saveAndFlush(varselbestilling);

		verify(notifikasjonPublisher, times(2)).sendNotifikasjon(any(Doknotifikasjon.class));
		verify(brukernotifikasjonBeskjedPublisher, times(2)).sendNotifikasjon(any(BeskjedInput.class), any(NokkelInput.class));

		verifyNoMoreInteractions(varselbestillingRepo);
	}

	private void assertOkMedKontaktinfo() {
		assertThat(UUID.fromString(bestilling.getVarselBestillingId()).toString(), is(bestilling.getVarselBestillingId()));
		verify(varselbestillingRepo).saveAndFlush(varselbestilling);

		verify(notifikasjonMedKontaktinfoPublisher, times(2)).sendVarsel(any(NotifikasjonMedkontaktInfo.class));

		verifyNoMoreInteractions(varselbestillingRepo);
	}
}