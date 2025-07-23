package no.nav.varsel.consumer.dkif;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dkif.support.PostPersonerRequest;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static no.nav.varsel.consumer.naistoken.NaisTexasRequestInterceptor.TARGET_SCOPE;
import static no.nav.varsel.util.MDCGenerate.getCallId;
import static no.nav.varsel.util.NavConstants.NAV_CALL_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class HentDigitalKontaktinformasjonConsumer {

	private final RestClient restClientTexas;
	private final ObjectMapper objectMapper;
	private final HentDigitalKontaktinformasjonMapper mapper;
	private final String digdirKrrScope;

	public HentDigitalKontaktinformasjonConsumer(VarselProperties varselProperties,
												RestClient restClientTexas) {
		this.mapper = new HentDigitalKontaktinformasjonMapper();
		this.restClientTexas = restClientTexas.mutate()
				.baseUrl(varselProperties.getEndpoints().getDigdirKrrProxy().getUrl())
				.defaultHeaders(httpHeaders -> {
					httpHeaders.set(NAV_CALL_ID, getCallId());
					httpHeaders.setContentType(APPLICATION_JSON);
				})
				.build();
		this.objectMapper = new ObjectMapper();
		this.digdirKrrScope = varselProperties.getEndpoints().getDigdirKrrProxy().getScope();
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 2))
	public KontaktregisterTo hentDigitalKontaktinformasjon(String personIdent) {
		final String fnrTrimmed = personIdent.strip();

		PostPersonerRequest postPersonRequest = PostPersonerRequest.builder().personidenter(List.of(fnrTrimmed)).build();

		DigitalKontaktInfoResponse response = restClientTexas.post()
				.uri("/rest/v1/personer?inkluderSikkerDigitalPost=true")
				.attribute(TARGET_SCOPE, digdirKrrScope)
				.body(postPersonRequest)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					ProblemDetail problemDetail = objectMapper.readValue(res.getBody(), ProblemDetail.class);
					log.warn("Feil mot digdir-krr-proxy:  status: {}, problemDetail: {}", res.getStatusCode(), problemDetail.getDetail());
				})
				.body(DigitalKontaktInfoResponse.class);

		if (isValidResponse(response, fnrTrimmed)) {
			KontaktregisterTo kontaktregisterTo = mapper.map(response.getPersoner().get(fnrTrimmed));
			kontaktregisterTo.cleanExpiredInfo();
			return kontaktregisterTo;
		} else {
			log.warn("Feil mot digdir-krr-proxy: {}", getErrorMsg(response, fnrTrimmed));
			return null;
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
}
