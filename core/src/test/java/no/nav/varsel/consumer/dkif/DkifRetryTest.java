package no.nav.varsel.consumer.dkif;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {DkifRetryTest.Config.class, HentDigitalKontaktinformasjonConsumer.class, AzureProperties.class})
@ActiveProfiles("itest")
public class DkifRetryTest {

	private static final String PERSON_ID = "id";

	@Autowired
	private HentDigitalKontaktinformasjonConsumer hentDigitalKontaktinformasjonConsumer;

	@Autowired
	private HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper;

	@Autowired
	private VarselKanalDecider varselKanalDecider;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	public static final Set<KanalCode> PREFERERT_KANAL = Sets.newHashSet(KanalCode.DITT_NAV);


	@EnableRetry
	@Configuration
	public static class Config {

		@Bean
		public VarselKanalDecider varselKanalDecider() {
			return mock(VarselKanalDecider.class);
		}

		@Bean
		public TokenConsumer tokenConsumer() {
			return (TokenConsumer) (String s) -> new TokenResponse();
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

	@Test
	public void shouldRetryOnExceptionhentDigitalKontaktinformasjonAndDecideKanal() {
		DigitalKontaktInfoResponse response = createResponse();
		ArrayList<KanalCode> kanalCodes = Lists.newArrayList(KanalCode.DITT_NAV);
		RestTemplate restTemplate = restTemplateBuilder.build();
		when(restTemplate.postForEntity(anyString(), any(), any(Class.class))).thenThrow(new RuntimeException()).thenReturn((new ResponseEntity(response, HttpStatus.OK)));
		when(hentDigitalKontaktinformasjonMapper.map(response.getPersoner().get(PERSON_ID))).thenReturn(new KontaktregisterTo());
		when(varselKanalDecider.decideKanaler(any(), any())).thenReturn(kanalCodes);

		hentDigitalKontaktinformasjonConsumer.hentDigitalKontaktinformasjonAndDecideKanal(PERSON_ID, PREFERERT_KANAL);

		verify(restTemplate, times(2)).postForEntity(anyString(), any(), any(Class.class));

	}

	private DigitalKontaktInfoResponse createResponse() {
		DigitalKontaktInfoResponse response = new DigitalKontaktInfoResponse();
		Map<String, DigitalKontaktInfoResponse.DigitalKontaktinfo> map = new HashMap<>();
		map.put(PERSON_ID, DigitalKontaktInfoResponse.DigitalKontaktinfo.builder().build());
		response.setPersoner(map);
		return response;
	}

}