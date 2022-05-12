package no.nav.varsel.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.ServicemeldingTestUtils;
import no.nav.varsel.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.service.support.Varselutsending;
import no.nav.varsel.service.support.VarselutsendingMapper;
import no.nav.varsel.service.support.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.service.tvarsel001.support.BrukernotifikasjonMapper;
import no.nav.varsel.service.tvarsel001.support.NotifikasjonMapper;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.EPOST;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.OVERSTYRT_PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.TLF;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createDittNavMalUtenFoerstegangstekst;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createDoknotifikasjonWithKanalAndBestillingsId;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createMaler;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createNokkelInputWithBestillingsId;
import static no.nav.varsel.service.support.ServicemeldingTestUtils.createVarselutsendingWithKanal;
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
	private VarselutsendingMapper varselutsendingMapper;

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

	private final Varselbestilling varselbestilling = new Varselbestilling();
	private final BestillVarselTo bestilling = new BestillVarselTo();
	private final KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();
	private final VarselInfoTo varselInfoTo = new VarselInfoTo();
	private final Doknotifikasjon doknotifikasjon = new Doknotifikasjon();
	private final BeskjedInput beskjedInput = new BeskjedInput();
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
		var varselutsendingEpost = createVarselutsendingWithKanal(KanalCode.EPOST);
		var varselutsendingSms = createVarselutsendingWithKanal(KanalCode.SMS);
		var varselutsendingList = List.of(varselutsendingEpost, varselutsendingSms);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);

		when(notifikasjonMapper.mapNotifikasjon(varselutsendingList, varselbestilling)).thenReturn(doknotifikasjon);

		servicemeldingService.bestillServicemelding(bestilling);

		assertOK();
	}

	@Test
	public void shouldBestillServicemeldingMedBrukernotifikasjon() {
		bestilling.setAktoerId(AKTOR_ID);
		varselInfoTo.setMaler(createMaler());

		var varselutsendingEpost = createVarselutsendingWithKanal(KanalCode.EPOST);
		var varselutsendingDittNav = createVarselutsendingWithKanal(KanalCode.DITT_NAV);

		var varselutsendingList = List.of(varselutsendingEpost, varselutsendingDittNav);

		var doknotifikasjonEpost = createDoknotifikasjonWithKanalAndBestillingsId(KanalCode.EPOST, "beaa22a6-6233-4d9b-97c0-fc6b174f2a60");
		var doknotifikasjonDittNav = createDoknotifikasjonWithKanalAndBestillingsId(null, "079d437a-dd4d-49e1-ac9e-dfcb13c9ce5f");

		var nokkelDittNav = createNokkelInputWithBestillingsId(doknotifikasjonDittNav.getBestillingsId());

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);

		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL_MED_DITT_NAV);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);

		when(notifikasjonMapper.mapNotifikasjon(varselutsendingList, varselbestilling)).thenReturn(doknotifikasjonEpost);

		when(brukernotifikasjonMapper.mapBeskjed(varselutsendingList.get(1))).thenReturn(beskjedInput);
		when(brukernotifikasjonMapper.mapNokkel(varselbestilling)).thenReturn(nokkelDittNav);

		servicemeldingService.bestillServicemelding(bestilling);

		assertOkMedBrukernotifikasjon(doknotifikasjonEpost, doknotifikasjonDittNav);
	}

	@Test
	void shouldNotSendBrukernotifikasjonToDittNavWithoutFoerstegangsvarselTekst() {
		bestilling.setAktoerId(AKTOR_ID);
		varselInfoTo.setMaler(createDittNavMalUtenFoerstegangstekst());

		var varselutsendingDittNav = createVarselutsendingWithKanal(KanalCode.DITT_NAV);
		var varselutsendingList = singletonList(varselutsendingDittNav);
		var doknotifikasjonDittNav = createDoknotifikasjonWithKanalAndBestillingsId(null, "079d437a-dd4d-49e1-ac9e-dfcb13c9ce5f");
		var nokkelDittNav = createNokkelInputWithBestillingsId(doknotifikasjonDittNav.getBestillingsId());

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);

		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL_MED_DITT_NAV);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(bestilling, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);

		servicemeldingService.bestillServicemelding(bestilling);

		verify(brukernotifikasjonBeskjedPublisher, times(0)).sendNotifikasjon(any(BeskjedInput.class), eq(nokkelDittNav));
	}

	@Test
	public void shouldBestillServicemeldingMedKontaktinfo() {
		bestilling.setAktoerId(AKTOR_ID);
		bestilling.setEpost("test@epost.no");

		var varselutsendingEpost = createVarselutsendingWithKanal(KanalCode.EPOST);
		var varselutsendingSms = createVarselutsendingWithKanal(KanalCode.SMS);
		var varselutsendingList = List.of(varselutsendingEpost, varselutsendingSms);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID)).thenReturn(varselInfoTo);
		when(varselKanalDecider.decideKanaler(any(KontaktregisterTo.class), eq(PREFERERT_KANAL))).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestillingFoerstegangVarselUtenRevarsel(eq(bestilling), eq(varselInfoTo), any(KontaktregisterTo.class))).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);
		when(notifikasjonMedkontaktInfoMapper.mapNotifikasjonMedKontaktinfo(varselutsendingList, varselbestilling, bestilling)).thenReturn(notifikasjonMedkontaktInfo);

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
		verify(notifikasjonPublisher, times(1)).sendNotifikasjon(any(Doknotifikasjon.class));
		verify(brukernotifikasjonBeskjedPublisher, never()).sendNotifikasjon(any(BeskjedInput.class), any(NokkelInput.class));
	}

	private void assertOkMedBrukernotifikasjon(Doknotifikasjon doknotifikasjonEpost, Doknotifikasjon doknotifikasjonDittNav) {
		verify(notifikasjonPublisher, times(1)).sendNotifikasjon(doknotifikasjonEpost);
		verify(notifikasjonPublisher, times(0)).sendNotifikasjon(doknotifikasjonDittNav);

		verify(brukernotifikasjonBeskjedPublisher, times(1)).sendNotifikasjon(
				any(BeskjedInput.class),
				eq(createNokkelInputWithBestillingsId(doknotifikasjonDittNav.getBestillingsId())));

		verify(brukernotifikasjonBeskjedPublisher, times(0)).sendNotifikasjon(
				any(BeskjedInput.class),
				eq(createNokkelInputWithBestillingsId(doknotifikasjonEpost.getBestillingsId())));
	}

	private void assertOkMedKontaktinfo() {
		verify(notifikasjonMedKontaktinfoPublisher, times(1)).sendVarsel(any(NotifikasjonMedkontaktInfo.class));
		verify(brukernotifikasjonBeskjedPublisher, never()).sendNotifikasjon(any(BeskjedInput.class), any(NokkelInput.class));
	}
}