package no.nav.varsel.consumer.dkif;

import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dkif.support.PostPersonerRequest;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static no.nav.varsel.util.MDCGenerate.getCallId;
import static no.nav.varsel.util.NavConstants.NAV_CALL_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class HentDigitalKontaktinformasjonConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(HentDigitalKontaktinformasjonConsumer.class);
	private final RestTemplate restTemplate;
	private final String dkiUrl;
	private final TokenConsumer tokenConsumer;
	private final HentDigitalKontaktinformasjonMapper mapper;
	private final VarselProperties varselProperties;

	@Autowired
	public HentDigitalKontaktinformasjonConsumer(VarselProperties varselProperties,
												 TokenConsumer tokenConsumer,
												 RestTemplateBuilder restTemplateBuilder,
												 ClientHttpRequestFactory clientHttpRequestFactory) {
		this.mapper = new HentDigitalKontaktinformasjonMapper();
		this.dkiUrl = varselProperties.getEndpoints().getDigdirKrrProxy().getUrl();
		this.tokenConsumer = tokenConsumer;
		this.restTemplate = restTemplateBuilder
				.connectTimeout(ofSeconds(5))
				.requestFactory(() -> clientHttpRequestFactory)
				.build();
		this.varselProperties = varselProperties;
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 2))
	public KontaktregisterTo hentDigitalKontaktinformasjon(String personIdent) {
		HttpHeaders headers = createHeaders();
		DigitalKontaktInfoResponse response;
		final String fnrTrimmed = personIdent.strip();
		try {
			PostPersonerRequest postPersonRequest = PostPersonerRequest.builder().personidenter(List.of(fnrTrimmed)).build();
			HttpEntity<String> request = new HttpEntity(postPersonRequest, headers);
			response = restTemplate.postForEntity(dkiUrl + "/rest/v1/personer?inkluderSikkerDigitalPost=true", request, DigitalKontaktInfoResponse.class).getBody();

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			LOG.warn(format("Feil mot DKIF %s: %s", e.getClass().getSimpleName(), e.getMessage()));
			return new KontaktregisterTo();
		}
		if (isValidResponse(response, fnrTrimmed)) {
			KontaktregisterTo kontaktregisterTo = mapper.map(response.getPersoner().get(fnrTrimmed));
			kontaktregisterTo.cleanExpiredInfo();
			return kontaktregisterTo;
		} else {
			LOG.warn(format("Feil mot DKIF: %s", getErrorMsg(response, fnrTrimmed)));
			return new KontaktregisterTo();
		}
	}

	private boolean isValidResponse(DigitalKontaktInfoResponse response, String fnr) {
		return response != null && response.getPersoner() != null && response.getPersoner().get(fnr) != null;
	}

	private String getErrorMsg(DigitalKontaktInfoResponse response, String fnr) {
		if (response == null || response.getFeil() == null) {
			return null;
		} else {
			return response.getFeil().get(fnr);
		}
	}

	private HttpHeaders createHeaders() {
		TokenResponse clientCredentialToken = tokenConsumer.getClientCredentialToken(varselProperties.getEndpoints().getDigdirKrrProxy().getScope());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_JSON);
		headers.setBearerAuth(clientCredentialToken.getAccess_token());
		headers.add(NAV_CALL_ID, getCallId());
		return headers;
	}
}
