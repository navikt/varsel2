package no.nav.varsel.tvarsel001.service.service;

import no.nav.tms.varsel.builder.BuilderEnvironment;
import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.DokmetConsumer;
import no.nav.varsel.consumer.dokmet.Varselinfo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.exception.functional.VarselTekstMissingException;
import no.nav.varsel.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import no.nav.varsel.tvarsel001.service.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.tvarsel001.service.service.to.BestillVarselTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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
import static no.nav.varsel.tvarsel001.service.service.support.ServicemeldingTestUtils.createVarselmal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class ServicemeldingServiceTest {

	private static final String TEKNISK = "teknisk";

	@Mock
	private AktoerService aktoerService;
	@Mock
	private DokmetConsumer dokmetConsumer;
	@Mock
	private HentDigitalKontaktinformasjonConsumer digitalKontaktinformasjonConsumer;
	@Mock
	private VarselKanalDecider varselKanalDecider;
	@Mock
	private VarselbestillingRepo varselbestillingRepo;
	@Mock
	private BrukernotifikasjonBeskjedPublisher brukernotifikasjonBeskjedPublisher;
	private VarselBestillingDomainMapper domainMapper;
	@Mock
	private VarselFletter varselFletter;

	private ServicemeldingService servicemeldingService;

	private final BestillVarselTo bestilling = new BestillVarselTo();
	private final KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();

	@BeforeEach
	public void setUp() {
		BuilderEnvironment.extend(Map.of(
				"NAIS_APP_NAME", "varsel2",
				"NAIS_NAMESPACE", "teamdokumenthandtering",
				"NAIS_CLUSTER_NAME", "test"
		));
		lenient().when(varselFletter.weaveText(anyString(), any())).thenReturn("en tekst i et varsel");

		domainMapper = Mockito.spy(new VarselBestillingDomainMapper(varselFletter));
		servicemeldingService = new ServicemeldingService(aktoerService, dokmetConsumer, digitalKontaktinformasjonConsumer, varselKanalDecider, domainMapper, varselbestillingRepo, brukernotifikasjonBeskjedPublisher);
		bestilling.setVarselBestillingId(null);
		bestilling.setPersonIdent(null);
		bestilling.setAktoerId(null);
		bestilling.setVarseltypeId(VARSELTYPE_ID);
	}

	@Test
	public void shouldBestillServicemelding() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.maler(Set.of(createVarselmal(EPOST), createVarselmal(DITT_NAV)))
				.build();
		bestilling.setAktoerId(AKTOR_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(Set.of(EPOST, DITT_NAV));

		servicemeldingService.bestillServicemelding(bestilling);

		verify(brukernotifikasjonBeskjedPublisher, times(1)).sendNotifikasjon(anyString(), anyString());
	}

	@Test
	public void shouldBestillServicemeldingForBestillingMedVarselbestillingId() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(PREFERERT_KANAL)
				.maler(Set.of(createVarselmal(EPOST), createVarselmal(SMS), createVarselmal(DITT_NAV)))
				.build();

		bestilling.setAktoerId(AKTOR_ID);
		bestilling.setVarselBestillingId(VARSELBESTILLING_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);

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
				.maler(Set.of(createVarselmal(EPOST), createVarselmal(SMS), createVarselmal(DITT_NAV)))
				.build();

		bestilling.setAktoerId(AKTOR_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(TestdataUtil.PREFERERT_KANAL);

		servicemeldingService.bestillServicemelding(bestilling);

		ArgumentCaptor<BestillVarselTo> captor = ArgumentCaptor.forClass(BestillVarselTo.class);
		verify(aktoerService, times(1)).findMissingAktoer(captor.capture());

		BestillVarselTo captorValue = captor.getValue();
		assertThat(captorValue.getVarselBestillingId()).isNotEmpty();
	}

	@Test
	void shouldNotSendBrukernotifikasjonToDittNavWithoutFoerstegangsvarselTekst() {
		var varselinfo = Varselinfo.builder()
				.preferertKanal(TestdataUtil.PREFERERT_KANAL_MED_DITT_NAV)
				.maler(Set.of(createDittNavMalUtenFoerstegangstekst(), createVarselmal(EPOST)))
				.build();

		bestilling.setAktoerId(AKTOR_ID);

		when(aktoerService.findMissingAktoer(bestilling)).thenReturn(newPersonIdent(FNR));
		when(dokmetConsumer.hentVarselinfo(VARSELTYPE_ID)).thenReturn(varselinfo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, TestdataUtil.PREFERERT_KANAL_MED_DITT_NAV)).thenReturn(TestdataUtil.PREFERERT_KANAL_MED_DITT_NAV);

		assertThrows(VarselTekstMissingException.class, () -> servicemeldingService.bestillServicemelding(bestilling));

		verify(brukernotifikasjonBeskjedPublisher, times(0)).sendNotifikasjon(eq(VARSELBESTILLING_ID), anyString());
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