package no.nav.varsel.wsconsumer.aktoer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;
import no.nav.varsel.domain.to.AktoerTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
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
@SpringApplicationConfiguration(classes = {AktoerConsumerRetryTest.Config.class, AktoerConsumer.class})
public class AktoerConsumerRetryTest {

	private static final String PERSON_ID = "id";
	private static final String AKTOER_ID = "aktoerId";

	@Inject
	private AktoerConsumer aktoerConsumer;

	@Inject
	private AktoerV2 aktoerV2Mock;

	@EnableRetry
	@Configuration
	public static class Config {
		@Bean
		public AktoerV2 aktoerV2Mock() {
			return mock(AktoerV2.class);
		}
	}

	@Test
	public void shouldRetryOnException() throws HentAktoerIdForIdentPersonIkkeFunnet, HentIdentForAktoerIdPersonIkkeFunnet {
		HentAktoerIdForIdentResponse aktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
		aktoerIdForIdentResponse.setAktoerId(AKTOER_ID);
		when(aktoerV2Mock.hentAktoerIdForIdent(persId(PERSON_ID))).thenThrow(new RuntimeException("tes")).thenReturn(aktoerIdForIdentResponse);

		AktoerTo aktoerTo = aktoerConsumer.hentIdent(AktoerTo.newPersonIdent(PERSON_ID));

		verify(aktoerV2Mock, times(2)).hentAktoerIdForIdent(Matchers.any());

	}

	private HentAktoerIdForIdentRequest persId(String ident) {
		HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest() {
			@Override
			public boolean equals(Object obj) {
				return obj instanceof HentAktoerIdForIdentRequest &&
						this.getIdent().equals(((HentAktoerIdForIdentRequest) obj).getIdent());
			}
		};
		request.setIdent(ident);
		return eq(request);
	}

	private HentIdentForAktoerIdRequest aktId(String ident) {
		HentIdentForAktoerIdRequest request = new HentIdentForAktoerIdRequest() {
			@Override
			public boolean equals(Object obj) {
				return obj instanceof HentIdentForAktoerIdRequest &&
						this.getAktoerId().equals(((HentIdentForAktoerIdRequest) obj).getAktoerId());
			}
		};
		request.setAktoerId(ident);
		return eq(request);
	}

}