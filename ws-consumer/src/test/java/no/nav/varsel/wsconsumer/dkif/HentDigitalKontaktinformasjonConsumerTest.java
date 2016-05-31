package no.nav.varsel.wsconsumer.dkif;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.KontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.Sikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link HentDigitalKontaktinformasjonConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class HentDigitalKontaktinformasjonConsumerTest {

	private static final String ID_OK = "ok";
	private static final String ID_404 = "404";
	private static final String ID_KON404 = "4042";
	private static final String ID_500 = "500";

	@Mock
	private DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1;
	@Mock
	private HentDigitalKontaktinformasjonMapper mapper;

	@InjectMocks
	private HentDigitalKontaktinformasjonConsumer consumer;
	private KontaktregisterTo kontaktregisterTo;

	@Before
	public void setUp() throws Exception {
		HentDigitalKontaktinformasjonResponse response = new HentDigitalKontaktinformasjonResponse();
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_OK))).thenReturn(response);
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_404)))
				.thenThrow(new HentDigitalKontaktinformasjonPersonIkkeFunnet("", new PersonIkkeFunnet()));
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_KON404)))
				.thenThrow(new HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet("", new KontaktinformasjonIkkeFunnet()));
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_500)))
				.thenThrow(new HentDigitalKontaktinformasjonSikkerhetsbegrensing("", new Sikkerhetsbegrensing()));

		kontaktregisterTo = new KontaktregisterTo();
		when(mapper.map(response)).thenReturn(kontaktregisterTo);
	}

	@Test
	public void shouldHentDigitalKontaktinfo() throws Exception {
		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjon(ID_OK);
		assertThat(kontaktregisterTo, is(this.kontaktregisterTo));
	}

	@Test
	public void shouldReturnNullOn_NotFound() throws Exception {
		assertThat(consumer.hentDigitalKontaktinformasjon(ID_404), nullValue());
	}

	@Test
	public void shouldReturnNullOn_NotFoundKontaktInfo() throws Exception {
		assertThat(consumer.hentDigitalKontaktinformasjon(ID_KON404), nullValue());
	}

	@Test
	public void shouldReturnNullOn_Sikkerhetsbegrensning() throws Exception {
		assertThat(consumer.hentDigitalKontaktinformasjon(ID_500), nullValue());
	}

	private HentDigitalKontaktinformasjonRequest reqId(String ident) {
		HentDigitalKontaktinformasjonRequest request = new HentDigitalKontaktinformasjonRequest(){
			@Override
			public boolean equals(Object obj) {
				return obj instanceof HentDigitalKontaktinformasjonRequest &&
						this.getPersonident().equals(((HentDigitalKontaktinformasjonRequest) obj).getPersonident());
			}
		};
		request.setPersonident(ident);
		return eq(request);
	}
}