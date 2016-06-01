package no.nav.varsel.service;

import static no.nav.varsel.domain.to.MottakerType.AKTOER;
import static no.nav.varsel.domain.to.MottakerType.PERSON;
import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.PREFERERT_KANAL;
import static no.nav.varsel.repo.TestdataUtil.VARSLINGSTYPE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.feil.PersonIkkeFunnet;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.repo.TestdataUtil;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.VarselutsendingToMapper;
import no.nav.varsel.service.tvarsel001.support.ServicemeldingDomainMapper;
import no.nav.varsel.service.tvarsel001.to.BestillServicemeldingTo;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.aktoer.support.AktoerIkkeFunnetException;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;


/**
 * Unit test for {@link ServicemeldingService}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServicemeldingServiceTest {

	private static final String FUNKSJONELL = "funksjonell";
	private static final String TEKNISK = "teknisk";

	@Mock
	private AktoerConsumer aktoerConsumer;
	@Mock
	private VarselInfoConsumer varselInfoConsumer;
	@Mock
	private HentDigitalKontaktinformasjonConsumer digitalKontaktinformasjonConsumer;

	@Mock
	private VarselutsendingProducer varselutsendingProducer;
	@Mock
	private VarselutsendingToMapper varselutsendingToMapper;

	@Mock
	private ServicemeldingDomainMapper domainMapper;
	@Mock
	private VarslelKanalDecider varslelKanalDecider;
	@Mock
	private VarselbestillingRepo varselbestillingRepo;

	@InjectMocks
	private ServicemeldingService servicemeldingService;

	private ArrayList<VarselutsendingTo> varselutsendingTos = Lists.newArrayList(new VarselutsendingTo(), new VarselutsendingTo());
	private Varselbestilling varselbestilling = new Varselbestilling();
	private BestillServicemeldingTo bestillServicemeldingTo = new BestillServicemeldingTo();
	private ArrayList<KanalCode> kanalCodes = new ArrayList<>();
	private KontaktregisterTo kontaktregisterTo = new KontaktregisterTo();
	private ArithmeticException teknisk = new ArithmeticException(TEKNISK);
	private AktoerTo aktoerTo = new AktoerTo(TestdataUtil.AKTOR_ID, AKTOER);
	private VarselInfoTo varselInfoTo = new VarselInfoTo();

	@Before
	public void setUp() throws Exception {
		// reset
		bestillServicemeldingTo.setPersonIdent(null);
		bestillServicemeldingTo.setAktoerId(null);
		bestillServicemeldingTo.setVarslingstype(VARSLINGSTYPE);

		varselInfoTo.setPreferertKanal(PREFERERT_KANAL);

		when(aktoerConsumer.hentIdent(eq(aktoerTo))).thenReturn(new AktoerTo(FNR, PERSON));
		when(varselInfoConsumer.hentVarselInfo(VARSLINGSTYPE)).thenReturn(varselInfoTo);
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(FNR)).thenReturn(kontaktregisterTo);
		when(varslelKanalDecider.decideKanaler(kontaktregisterTo, varselInfoTo.getPreferertKanal())).thenReturn(kanalCodes);
		when(domainMapper.mapToDomain(bestillServicemeldingTo, varselInfoTo, kontaktregisterTo)).thenReturn(varselbestilling);
		when(varselutsendingToMapper.map(eq(varselbestilling), eq(aktoerTo))).thenReturn(varselutsendingTos);
	}

	@Test
	public void shouldBestillServicemelding() throws Exception {
		bestillServicemeldingTo.setAktoerId(AKTOR_ID);
		servicemeldingService.bestillServicemelding(bestillServicemeldingTo);

		assertOK();
	}

	@Test(expected = AktoerIkkeFunnetException.class)
	public void shouldThrowAktoerIkkeFunnetExceptionForFunksjonellFeilAktoer() throws Exception {
		when(aktoerConsumer.hentIdent(eq(new AktoerTo(FUNKSJONELL, AKTOER))))
				.thenThrow(new HentAktoerIdForIdentPersonIkkeFunnet("", new PersonIkkeFunnet()));

		bestillServicemeldingTo.setAktoerId(FUNKSJONELL);
		servicemeldingService.bestillServicemelding(bestillServicemeldingTo);
	}

	@Test(expected = ArithmeticException.class)
	public void shouldThrowExceptionIfTekniskForTekniskFeilAktoer() throws Exception {
		when(aktoerConsumer.hentIdent(eq(new AktoerTo(TEKNISK, AKTOER)))).thenThrow(teknisk);

		bestillServicemeldingTo.setAktoerId(TEKNISK);
		servicemeldingService.bestillServicemelding(bestillServicemeldingTo);
	}

	@Test(expected = ArithmeticException.class)
	public void shouldThrowTekniskForTekniskFeilDkif() throws Exception {
		when(digitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(TEKNISK)).thenThrow(teknisk);
		bestillServicemeldingTo.setPersonIdent(TEKNISK);
		servicemeldingService.bestillServicemelding(bestillServicemeldingTo);
	}

	@Test
	public void shouldHandleNullDkifResponse() throws Exception {
		String ident = "some other ident";
		when(aktoerConsumer.hentIdent(eq(new AktoerTo(ident, AKTOER)))).thenReturn(new AktoerTo("otherperson not in dkif", PERSON));
		when(varslelKanalDecider.decideKanaler(any(KontaktregisterTo.class), eq(varselInfoTo.getPreferertKanal()))).thenReturn(kanalCodes);
		when(domainMapper.mapToDomain(eq(bestillServicemeldingTo), eq(varselInfoTo), any(KontaktregisterTo.class))).thenReturn(varselbestilling);

		bestillServicemeldingTo.setPersonIdent(ident);
		servicemeldingService.bestillServicemelding(bestillServicemeldingTo);
	}

	private void assertOK() {
		assertThat(kontaktregisterTo.getKanaler(), is(kanalCodes));

		verify(varselbestillingRepo).saveAndFlush(varselbestilling);

		verify(varselutsendingProducer).produce(varselutsendingTos.get(0));
		verify(varselutsendingProducer).produce(varselutsendingTos.get(1));
		verifyNoMoreInteractions(varselutsendingProducer, varselbestillingRepo);
	}
}