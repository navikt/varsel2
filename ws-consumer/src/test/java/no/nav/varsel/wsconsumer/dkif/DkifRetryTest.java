package no.nav.varsel.wsconsumer.dkif;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Unit test for {@link AktoerConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DkifRetryTest.Config.class, HentDigitalKontaktinformasjonConsumer.class})
public class DkifRetryTest {

	private static final String PERSON_ID = "id";

	@Inject
	private HentDigitalKontaktinformasjonConsumer hentDigitalKontaktinformasjonConsumer;

	@Inject
	private DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1Mock;

	@EnableRetry
	@Configuration
	public static class Config {

		@Bean
		public VarselKanalDecider varselKanalDecider() {
			return mock(VarselKanalDecider.class);
		}

		;

		@Bean
		public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1() {
			return mock(DigitalKontaktinformasjonV1.class);
		}

		@Bean
		public HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper() {
			return mock(HentDigitalKontaktinformasjonMapper.class);
		}

		;
	}

	@Test
	public void shouldRetryOnException() throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonPersonIkkeFunnet {
		when(digitalKontaktinformasjonV1Mock.hentDigitalKontaktinformasjon(any())).thenThrow(new RuntimeException());

		try {
			hentDigitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjon(PERSON_ID);
			fail();
		} catch (Exception e) {

		}

		verify(digitalKontaktinformasjonV1Mock, times(5)).hentDigitalKontaktinformasjon(any());

	}

}