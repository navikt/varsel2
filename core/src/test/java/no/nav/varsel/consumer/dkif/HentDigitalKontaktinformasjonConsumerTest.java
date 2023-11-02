package no.nav.varsel.consumer.dkif;

import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
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

@SpringBootTest(classes = {
		HentDigitalKontaktinformasjonConsumerTest.Config.class,
		HentDigitalKontaktinformasjonConsumer.class
})
@AutoConfigureWireMock(port = 0)
public class HentDigitalKontaktinformasjonConsumerTest {

	private static final String ID_OK = "ok";
	private static final String ID_404 = "404";
	private static final String ID_KON404 = "4042";
	private static final String ID_500 = "500";
	private static final String PERSON_ID = "id";
	public static final Set<KanalCode> PREFERERT_KANAL = Set.of(DITT_NAV);
	public static final Set<KanalCode> PREFERERT_KANAL_2 = Set.of(SMS);
	private static final String MOBILTELEFONNUMMER = "11111111";

	@Autowired
	private HentDigitalKontaktinformasjonMapper mapper;

	@Autowired
	private VarselKanalDecider varselKanalDecider;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Autowired
	private HentDigitalKontaktinformasjonConsumer consumer;
	private KontaktregisterTo kontaktregisterTo;
	private final Set<KanalCode> kanalCodes = Set.of(DITT_NAV);
	private final Set<KanalCode> kanalCodes2 = Set.of(SMS);


	@Configuration
	public static class Config {

		@Bean
		public VarselKanalDecider varselKanalDecider() {
			return mock(VarselKanalDecider.class);
		}

		@Bean
		public TokenConsumer tokenConsumer() {
			return (String s) -> new TokenResponse();
		}

		@Bean
		public RestTemplateBuilder restTemplateBuilder() {
			RestTemplateBuilder rtb = mock(RestTemplateBuilder.class);
			RestTemplate restTemplate = mock(RestTemplate.class);
			when(rtb.setConnectTimeout(any())).thenReturn(rtb);
			when(rtb.setReadTimeout(any())).thenReturn(rtb);
			when(rtb.build()).thenReturn(restTemplate);
			return rtb;
		}

		@Bean
		public HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper() {
			return mock(HentDigitalKontaktinformasjonMapper.class);
		}

		@Bean
		public AzureProperties azureProperties() {
			AzureProperties azureProperties = new AzureProperties();
			azureProperties.setAppScopeDigdirKrr("digdir");
			azureProperties.setAppClientId("clientId");
			azureProperties.setOpenidConfigTokenEndpoint("url");
			azureProperties.setAppClientSecret("secret");
			return azureProperties;
		}

	}

	@BeforeEach
	public void setUp() throws Exception {
		kontaktregisterTo = new KontaktregisterTo();
	}

	@Test
	public void shouldHentDigitalKontaktinfo() {
		when(restTemplateBuilder.build().postForEntity(anyString(), any(), any(Class.class))).thenReturn((new ResponseEntity(digitalKontaktInfoResponse(), HttpStatus.OK)));
		when(mapper.map(any())).thenReturn(createKontaktregister());

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjon(ID_OK);

		assertThat(kontaktregisterTo, is(this.kontaktregisterTo));
	}

	@Test
	public void shouldHentDigitalKontaktinfoAndDecideKanaler() {
		when(restTemplateBuilder.build().postForEntity(anyString(), any(), any(Class.class))).thenReturn((new ResponseEntity(digitalKontaktInfoResponse(), HttpStatus.OK)));
		when(mapper.map(digitalKontaktInfoResponse().getPersoner().get(ID_OK))).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(kanalCodes);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjonAndDecideKanal(ID_OK, PREFERERT_KANAL);

		Assertions.assertThat(kontaktregisterTo.getKanaler()).isEqualTo(kanalCodes);
	}

	@Test
	public void shouldHentDigitalKontaktinfoAndDecideKanalerWhenNullResponseFromDKif() {
		when(restTemplateBuilder.build().postForEntity(anyString(), any(), any(Class.class))).thenReturn((new ResponseEntity(digitalKontaktInfoFeilResponse(), HttpStatus.OK)));
		when(varselKanalDecider.decideKanaler(any(KontaktregisterTo.class), eq(PREFERERT_KANAL_2))).thenReturn(kanalCodes2);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjonAndDecideKanal(ID_404, PREFERERT_KANAL_2);

		assertThat(kontaktregisterTo.getKanaler(), is(kanalCodes));
	}

	@Test
	public void shouldReturnNullOn_NotFound() {
		when(restTemplateBuilder.build().postForEntity(any(), any(), any())).thenReturn((new ResponseEntity(digitalKontaktInfoFeilResponse(), HttpStatus.OK)));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_404);

		assertKontaktRegisterToIsEmpty(actual);
	}

	@Test
	public void shouldReturnNullOn_NotFoundKontaktInfo() throws Exception {
		when(restTemplateBuilder.build().postForEntity(anyString(), any(), any(Class.class))).thenReturn((new ResponseEntity(digitalKontaktInfoFeilResponse(), HttpStatus.OK)));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_KON404);

		assertKontaktRegisterToIsEmpty(actual);
	}

	@Test
	public void shouldReturnNullOn_Sikkerhetsbegrensning() {
		when(restTemplateBuilder.build().postForEntity(anyString(), any(), any(Class.class))).thenReturn((new ResponseEntity(digitalKontaktInfoFeilResponse(), HttpStatus.OK)));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_500);

		assertKontaktRegisterToIsEmpty(actual);
	}

	private void assertKontaktRegisterToIsEmpty(KontaktregisterTo actual) {
		assertThat(actual.getKanaler(), nullValue());
		assertThat(actual.getEpostadresse(), nullValue());
		assertThat(actual.getMobiltelefonnummer(), nullValue());
	}

	public KontaktregisterTo createKontaktregister() {
		return KontaktregisterTo.builder()
				.kanaler(Collections.singleton(SMS))
				.mobiltelefonnummer(MOBILTELEFONNUMMER)
				.build();
	}

	private DigitalKontaktInfoResponse digitalKontaktInfoFeilResponse() {
		Map<String, String> feil = new HashMap<>();
		feil.put(ID_OK, "Person ikke funnet");
		return DigitalKontaktInfoResponse.builder()
				.feil(feil)
				.build();
	}

	private DigitalKontaktInfoResponse digitalKontaktInfoResponse() {
		Map<String, DigitalKontaktInfoResponse.DigitalKontaktinfo> map = new HashMap<>();
		map.put(ID_OK, DigitalKontaktInfoResponse.DigitalKontaktinfo.builder().build());
		return DigitalKontaktInfoResponse.builder()
				.personer(map)
				.build();
	}

}