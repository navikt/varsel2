package no.nav.varsel.consumer.dkif;

import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dkif.support.PostPersonerRequest;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static no.nav.varsel.consumer.pdl.helper.DomainConstants.APP_NAME;
import static no.nav.varsel.consumer.pdl.helper.DomainConstants.BEARER_PREFIX;
import static no.nav.varsel.util.MDCGenerate.CALL_ID;

/**
 * HentDigitalKontaktinformasjon Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class HentDigitalKontaktinformasjonConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(HentDigitalKontaktinformasjonConsumer.class);
	private final RestTemplate restTemplate;
	private final VarselKanalDecider varselKanalDecider;
	private final String dkiUrl;
	private final TokenConsumer tokenConsumer;
	private final HentDigitalKontaktinformasjonMapper mapper;
	private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

	@Autowired
	public HentDigitalKontaktinformasjonConsumer(VarselKanalDecider varselKanalDecider,
												 HentDigitalKontaktinformasjonMapper mapper,
												 @Value("${digdir_krr_proxy_url}") String dkiUrl,
												 TokenConsumer tokenConsumer,
												 RestTemplateBuilder restTemplateBuilder) {
		this.mapper = mapper;
		this.varselKanalDecider = varselKanalDecider;
		this.dkiUrl = dkiUrl;
		this.tokenConsumer = tokenConsumer;
		this.restTemplate = restTemplateBuilder
				.setConnectTimeout(Duration.ofSeconds(5))
				.setReadTimeout(Duration.ofSeconds(20))
				.build();
	}



	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 2))
	public KontaktregisterTo hentDigitalKontaktinformasjonAndDecideKanal(String personIdent, Set<KanalCode> preferertKanal) {
		KontaktregisterTo kontaktregisterTo = hentDigitalKontaktinformasjon(personIdent);

		Collection<KanalCode> kanaler = varselKanalDecider.decideKanaler(kontaktregisterTo, preferertKanal);
		kontaktregisterTo.setKanaler(kanaler);
		return kontaktregisterTo;
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 2))
	public KontaktregisterTo hentDigitalKontaktinformasjon(String personIdent) {
		HttpHeaders headers = createHeaders();
		DigitalKontaktInfoResponse response;
		final String fnrTrimmed = personIdent.strip();
		try {
			PostPersonerRequest postPersonRequest = PostPersonerRequest.builder().personidenter(Arrays.asList(fnrTrimmed)).build();
			HttpEntity<String> request = new HttpEntity(postPersonRequest, headers);
			response = restTemplate.postForEntity(dkiUrl + "/rest/v1/personer?inkluderSikkerDigitalPost=true", request, DigitalKontaktInfoResponse.class).getBody();

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			LOG.warn(String.format("Feil mot DKIF %s: %s", e.getClass().getSimpleName(), e.getMessage()));
			return new KontaktregisterTo();
		}
		if (isValidRespons(response, fnrTrimmed)) {
			KontaktregisterTo kontaktregisterTo = mapper.map(response.getPersoner().get(fnrTrimmed));
			return kontaktregisterTo;
		} else {
			LOG.warn(String.format("Feil mot DKIF: %s", getErrorMsg(response, fnrTrimmed)));
			return new KontaktregisterTo();
		}
	}

	private boolean isValidRespons(DigitalKontaktInfoResponse response, String fnr) {
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
		TokenResponse clientCredentialToken = tokenConsumer.getClientCredentialToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + clientCredentialToken.getAccess_token());
		headers.add(NAV_CONSUMER_ID, APP_NAME);
		headers.add(CALL_ID, MDC.get(CALL_ID));
		return headers;
	}
}
