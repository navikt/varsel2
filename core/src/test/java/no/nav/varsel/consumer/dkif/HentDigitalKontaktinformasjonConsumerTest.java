package no.nav.varsel.consumer.dkif;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.KontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.Sikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonRequest;
import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link HentDigitalKontaktinformasjonConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@SpringBootTest(classes = {HentDigitalKontaktinformasjonConsumerTest.Config.class, HentDigitalKontaktinformasjonConsumer.class})
public class HentDigitalKontaktinformasjonConsumerTest {

	private static final String ID_OK = "ok";
	private static final String ID_404 = "404";
	private static final String ID_KON404 = "4042";
	private static final String ID_500 = "500";
	public static final Set<KanalCode> PREFERERT_KANAL = Sets.newHashSet(DITT_NAV);
	public static final Set<KanalCode> PREFERERT_KANAL_2 = Sets.newHashSet(SMS);

	@Autowired
	private HentDigitalKontaktinformasjonMapper mapper;

	@Autowired
	private VarselKanalDecider varselKanalDecider;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Autowired
	private HentDigitalKontaktinformasjonConsumer consumer;
	private KontaktregisterTo kontaktregisterTo;
	private DigitalKontaktInfoResponse response;
	private DigitalKontaktInfoResponse responseFeil;
	private final ArrayList<KanalCode> kanalCodes = Lists.newArrayList(DITT_NAV);
	private final ArrayList<KanalCode> kanalCodes2 = Lists.newArrayList(SMS);

	@Configuration
	public static class Config {

		@Bean
		public VarselKanalDecider varselKanalDecider() {
			return mock(VarselKanalDecider.class);
		}

		@Bean
		public TokenConsumer tokenConsumer() {
			return (TokenConsumer) () -> new TokenResponse();
		}

		@Bean
		public RestTemplateBuilder restTemplateBuilder() {
			RestTemplateBuilder rtb = mock(RestTemplateBuilder.class);
			RestTemplate restTemplate = mock(RestTemplate.class);
			when(rtb.setReadTimeout(any())).thenReturn(rtb);
			when(rtb.setConnectTimeout(any())).thenReturn(rtb);
			when(rtb.build()).thenReturn(restTemplate);
			return rtb;
		}

		@Bean
		public HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper() {
			return mock(HentDigitalKontaktinformasjonMapper.class);
		}
	}

	@BeforeEach
	public void setUp() throws Exception {
		kontaktregisterTo = new KontaktregisterTo();
		response = new DigitalKontaktInfoResponse();
		Map<String, DigitalKontaktInfoResponse.DigitalKontaktinfo> map = new HashMap<>();
		map.put(ID_OK, DigitalKontaktInfoResponse.DigitalKontaktinfo.builder().build());
		response.setPersoner(map);
		responseFeil = new DigitalKontaktInfoResponse();
		Map<String, String> feil = new HashMap<>();
		feil.put(ID_OK, "Person ikke funnet");
		responseFeil.setFeil(feil);
	}

	@Test
	public void shouldHentDigitalKontaktinfo() throws Exception {
		when(restTemplateBuilder.build().postForEntity(anyString(),any(), any(Class.class))).thenReturn((new ResponseEntity(response, HttpStatus.OK)));
		when(mapper.map(response.getPersoner().get(ID_OK))).thenReturn(kontaktregisterTo);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjon(ID_OK);

		assertThat(kontaktregisterTo, is(this.kontaktregisterTo));
	}

	@Test
	public void shouldHentDigitalKontaktinfoAndDecideKanaler() throws Exception {
		when(restTemplateBuilder.build().postForEntity(anyString(),any(), any(Class.class))).thenReturn((new ResponseEntity(response, HttpStatus.OK)));
		when(mapper.map(response.getPersoner().get(ID_OK))).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(kanalCodes);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjonAndDecideKanal(ID_OK, PREFERERT_KANAL);

		assertThat(kontaktregisterTo.getKanaler(), is(kanalCodes));
	}

	@Test
	public void shouldHentDigitalKontaktinfoAndDecideKanalerWhenNullResponseFromDKif() throws Exception {
		when(restTemplateBuilder.build().postForEntity(anyString(),any(), any(Class.class))).thenReturn((new ResponseEntity(responseFeil, HttpStatus.OK)));
		when(varselKanalDecider.decideKanaler(any(KontaktregisterTo.class), eq(PREFERERT_KANAL_2))).thenReturn(kanalCodes2);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjonAndDecideKanal(ID_404, PREFERERT_KANAL_2);

		assertThat(kontaktregisterTo.getKanaler(), is(kanalCodes2));
	}

	@Test
	public void shouldReturnNullOn_NotFound() throws Exception {
		when(restTemplateBuilder.build().postForEntity(any(), any(), any())).thenReturn((new ResponseEntity(responseFeil, HttpStatus.OK)));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_404);

		assertKontaktRegisterToIsEmpty(actual);
	}

	@Test
	public void shouldReturnNullOn_NotFoundKontaktInfo() throws Exception {
		when(restTemplateBuilder.build().postForEntity(anyString(),any(), any(Class.class))).thenReturn((new ResponseEntity(responseFeil, HttpStatus.OK)));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_KON404);

		assertKontaktRegisterToIsEmpty(actual);
	}

	@Test
	public void shouldReturnNullOn_Sikkerhetsbegrensning() throws Exception {
		when(restTemplateBuilder.build().postForEntity(anyString(),any(), any(Class.class))).thenReturn((new ResponseEntity(responseFeil, HttpStatus.OK)));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_500);

		assertKontaktRegisterToIsEmpty(actual);
	}

	private void assertKontaktRegisterToIsEmpty(KontaktregisterTo actual) {
		assertThat(actual.getKanaler(), nullValue());
		assertThat(actual.getEpostadresse(), nullValue());
		assertThat(actual.getMobiltelefonnummer(), nullValue());
	}

	private HentDigitalKontaktinformasjonRequest reqId(String ident) {
		HentDigitalKontaktinformasjonRequest request = new HentDigitalKontaktinformasjonRequest() {
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