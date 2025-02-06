package no.nav.varsel.tvarsel001.service.service;

import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.DokmetConsumer;
import no.nav.varsel.consumer.dokmet.Varselinfo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.tvarsel001.service.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.tvarsel001.service.service.support.Varselutsending;
import no.nav.varsel.tvarsel001.service.service.support.VarselutsendingMapper;
import no.nav.varsel.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.tvarsel001.service.service.to.BestillVarselTo;
import no.nav.varsel.tvarsel001.service.service.support.BrukernotifikasjonMapper;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createDittNavMalUtenFoerstegangstekst;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createNokkelInputWithBestillingsId;
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createVarselutsendingWithKanal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class ServicemeldingServiceTest {

	private static final String TEKNISK = "teknisk";

	private static final Varselutsending VARSELUTSENDING_DITT_NAV = createVarselutsendingWithKanal(DITT_NAV);
	private static final Varselutsending VARSELUTSENDING_EPOST = createVarselutsendingWithKanal(EPOST);
	private static final Varselutsending VARSELUTSENDING_SMS = createVarselutsendingWithKanal(SMS);

	@Mock
	private AktoerService aktoerService;
	@Mock
	private DokmetConsumer dokmetConsumer;
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
	private BrukernotifikasjonBeskjedPublisher brukernotifikasjonBeskjedPublisher;
	@Mock
	private BrukernotifikasjonMapper brukernotifikasjonMapper;

	@InjectMocks
	private ServicemeldingService servicemeldingService;

	private final Varselbestilling varselbestilling = new Varselbestilling();
	private final BestillVarselTo bestilling = new BestillVarselTo();
	private final KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();
	private final BeskjedInput beskjedInput = new BeskjedInput();

	@BeforeEach
	public void setUp() {
		bestilling.setVarselBestillingId(null);
		bestilling.setPersonIdent(null);
		bestilling.setAktoerId(null);
		bestilling.setVarseltypeId(VARSELTYPE_ID);
	}

	@Test
	public void shouldBestillServicemelding() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.build();
		bestilling.setAktoerId(AKTOR_ID);

		var varselutsendingList = List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_SMS, VARSELUTSENDING_DITT_NAV);
		var nokkelDittNav = createNokkelInputWithBestillingsId(VARSELBESTILLING_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestilling(bestilling, varselinfo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);
		when(brukernotifikasjonMapper.mapBeskjed(varselutsendingList)).thenReturn(beskjedInput);
		when(brukernotifikasjonMapper.mapNokkel(varselbestilling)).thenReturn(nokkelDittNav);

		servicemeldingService.bestillServicemelding(bestilling);

		verify(brukernotifikasjonBeskjedPublisher, times(1)).sendNotifikasjon(any(BeskjedInput.class), any(NokkelInput.class));
	}

	@Test
	public void shouldBestillServicemeldingForBestillingMedVarselbestillingId() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.build();

		bestilling.setAktoerId(AKTOR_ID);
		bestilling.setVarselBestillingId(VARSELBESTILLING_ID);

		var varselutsendingList = List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_SMS, VARSELUTSENDING_DITT_NAV);
		var nokkelDittNav = createNokkelInputWithBestillingsId(VARSELBESTILLING_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestilling(bestilling, varselinfo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);
		when(brukernotifikasjonMapper.mapBeskjed(varselutsendingList)).thenReturn(beskjedInput);
		when(brukernotifikasjonMapper.mapNokkel(varselbestilling)).thenReturn(nokkelDittNav);

		servicemeldingService.bestillServicemelding(bestilling);

		ArgumentCaptor<BestillVarselTo> captor = ArgumentCaptor.forClass(BestillVarselTo.class);
		verify(aktoerService, times(1)).findMissingAktoer(captor.capture());

		BestillVarselTo captorValue = captor.getValue();
		assertThat(captorValue.getVarselBestillingId()).isEqualTo(VARSELBESTILLING_ID);
	}

	@Test
	public void shouldBestillServicemeldingForBestillingUtenVarselbestillingId() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.build();

		bestilling.setAktoerId(AKTOR_ID);

		var varselutsendingList = List.of(VARSELUTSENDING_EPOST, VARSELUTSENDING_SMS, VARSELUTSENDING_DITT_NAV);
		var nokkelDittNav = createNokkelInputWithBestillingsId(VARSELBESTILLING_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);
		when(domainMapper.mapVarselbestilling(bestilling, varselinfo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);
		when(brukernotifikasjonMapper.mapBeskjed(varselutsendingList)).thenReturn(beskjedInput);
		when(brukernotifikasjonMapper.mapNokkel(varselbestilling)).thenReturn(nokkelDittNav);

		servicemeldingService.bestillServicemelding(bestilling);

		ArgumentCaptor<BestillVarselTo> captor = ArgumentCaptor.forClass(BestillVarselTo.class);
		verify(aktoerService, times(1)).findMissingAktoer(captor.capture());

		BestillVarselTo captorValue = captor.getValue();
		assertThat(captorValue.getVarselBestillingId()).isNotEmpty();
	}

	@Test
	void shouldNotSendBrukernotifikasjonToDittNavWithoutFoerstegangsvarselTekst() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.maler(createDittNavMalUtenFoerstegangstekst())
				.build();

		bestilling.setAktoerId(AKTOR_ID);

		var varselutsendingList = singletonList(VARSELUTSENDING_DITT_NAV);
		var nokkelDittNav = createNokkelInputWithBestillingsId(VARSELBESTILLING_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL_MED_DITT_NAV);
		when(domainMapper.mapVarselbestilling(bestilling, varselinfo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingMapper.map(eq(varselbestilling))).thenReturn(varselutsendingList);

		servicemeldingService.bestillServicemelding(bestilling);

		verify(brukernotifikasjonBeskjedPublisher, times(0)).sendNotifikasjon(any(BeskjedInput.class), eq(nokkelDittNav));
	}

	@Test
	public void shouldThrowTekniskForTekniskFeilDkif() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.build();

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(TEKNISK));
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(TEKNISK)).thenThrow(new ArithmeticException(TEKNISK));

		Executable executable = () -> servicemeldingService.bestillServicemelding(bestilling);
		assertThrows(ArithmeticException.class, executable);
	}

	@Test
	public void throwsInaktivVarselmalExceptionForInaktivVarselmal() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.inaktiv(true)
				.varseltypeId(VARSELTYPE_ID)
				.build();

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		bestilling.setTestvarsel(false);

		Executable executable = () -> servicemeldingService.bestillServicemelding(bestilling);

		Exception exception = assertThrows(VarselInaktivVarselmalException.class, executable);
		assertTrue(exception.getMessage().contains("Det er ikke mulig å bestille servicemelding for mottaker med mottakerId=" +
				FNR + " og bestillingId="));
		assertTrue(exception.getMessage().contains(" med inaktiv varselmal med varseltypeId=" + TestdataUtil.VARSELTYPE_ID + "."));
	}

	@Test
	public void doesNotStoreVarselbestillingWhenInaktivVarselmal() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.inaktiv(true)
				.varseltypeId(VARSELTYPE_ID)
				.build();

		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		bestilling.setTestvarsel(false);

		try {
			servicemeldingService.bestillServicemelding(bestilling);
			fail();
		} catch (VarselInaktivVarselmalException ive) {
			verify(varselbestillingRepo, never()).saveAndFlush(any());
		}
	}

	@Test
	public void preferertKanalIsOverreidenWhenTestVarsel() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.inaktiv(false)
				.build();

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);

		bestilling.setTestvarsel(true);

		servicemeldingService.bestillServicemelding(bestilling);

		verify(domainMapper, times(1)).mapVarselbestilling(bestilling, varselinfo.withPreferertKanal(Set.of(KanalCode.values())), kontaktregisterTo);
		verify(domainMapper, never()).mapVarselbestilling(bestilling, varselinfo.withPreferertKanal(PREFERERT_KANAL), kontaktregisterTo);
	}

	@Test
	public void preferertKanallisteNotOverridenForNormalVarsler() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.inaktiv(false)
				.build();

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		bestilling.setTestvarsel(false);

		servicemeldingService.bestillServicemelding(bestilling);

		verify(domainMapper, never()).mapVarselbestilling(bestilling, varselinfo.withPreferertKanal(Set.of(KanalCode.values())), kontaktregisterTo);
		verify(domainMapper, times(1)).mapVarselbestilling(bestilling, varselinfo.withPreferertKanal(PREFERERT_KANAL), kontaktregisterTo);
	}

	@Test
	public void shouldThrowFunctionalForVarselbestilling_VarselbestillingUtloept() {
		LocalDateTime pastTime = LocalDateTime.now().minusDays(1);

		bestilling.setUtloepstidspunkt(pastTime);

		Executable executable = () -> servicemeldingService.bestillServicemelding(bestilling);
		Exception exception = assertThrows(VarselbestillingUtloeptException.class, executable);
		assertTrue(exception.getMessage().contains("Varselbestilling has utloepstidspunkt=" + pastTime));
	}

}