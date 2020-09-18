package no.nav.varsel.wsconsumer.dkif;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AktoerConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DkifRetryTest.Config.class, HentDigitalKontaktinformasjonConsumer.class})
public class DkifRetryTest {

	private static final String PERSON_ID = "id";

	@Inject
	private HentDigitalKontaktinformasjonConsumer hentDigitalKontaktinformasjonConsumer;

	@Inject
	private DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1Mock;

	@Inject
	private HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper;

	@Inject
	private VarselKanalDecider varselKanalDecider;

	public static final Set<KanalCode> PREFERERT_KANAL = Sets.newHashSet(KanalCode.DITT_NAV);


	@EnableRetry
	@Configuration
	public static class Config {

		@Bean
		public VarselKanalDecider varselKanalDecider() {
			return mock(VarselKanalDecider.class);
		}

		@Bean
		public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1() {
			return mock(DigitalKontaktinformasjonV1.class);
		}

		@Bean
		public HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper() {
			return mock(HentDigitalKontaktinformasjonMapper.class);
		}
	}

	@After
	public void resetMocks() {
		Mockito.reset(digitalKontaktinformasjonV1Mock);
	}

	@Test
	public void shouldRetryOnException() throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonPersonIkkeFunnet {
		HentDigitalKontaktinformasjonResponse response = new HentDigitalKontaktinformasjonResponse();

		when(digitalKontaktinformasjonV1Mock.hentDigitalKontaktinformasjon(any(HentDigitalKontaktinformasjonRequest.class))).thenThrow(new RuntimeException()).thenReturn(response);
		when(hentDigitalKontaktinformasjonMapper.map(response)).thenReturn(new KontaktregisterTo());

		hentDigitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(PERSON_ID);

		verify(digitalKontaktinformasjonV1Mock, times(2)).hentDigitalKontaktinformasjon(any());

	}

	@Test
	public void shouldRetryOnExceptionhentDigitalKontaktinformasjonAndDecideKanal() throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonPersonIkkeFunnet {
		HentDigitalKontaktinformasjonResponse response = new HentDigitalKontaktinformasjonResponse();
		ArrayList<KanalCode> kanalCodes = Lists.newArrayList(KanalCode.DITT_NAV);

		when(digitalKontaktinformasjonV1Mock.hentDigitalKontaktinformasjon(any(HentDigitalKontaktinformasjonRequest.class))).thenThrow(new RuntimeException()).thenReturn(response);
		when(hentDigitalKontaktinformasjonMapper.map(response)).thenReturn(new KontaktregisterTo());
		when(varselKanalDecider.decideKanaler(any(), any())).thenReturn(kanalCodes);

		hentDigitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjonAndDecideKanal(PERSON_ID, PREFERERT_KANAL);

		verify(digitalKontaktinformasjonV1Mock, times(2)).hentDigitalKontaktinformasjon(any());

	}

}