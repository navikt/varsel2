package no.nav.varsel.consumer.dokmet;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dokmet.api.tkat021.VarselInfoTo;
import no.nav.varsel.config.VarselProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static java.lang.String.format;
import static no.nav.varsel.consumer.config.cache.LokalCacheConfig.DOKMET_CACHE;
import static no.nav.varsel.consumer.dokmet.VarselinfoMapper.mapToVarselinfo;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class DokmetConsumer {

	private final RestClient restClient;
	private final ObjectMapper objectMapper;

	public DokmetConsumer(VarselProperties varselProperties,
						  RestClient.Builder restClientBuilder,
						  ObjectMapper objectMapper) {
		this.restClient = restClientBuilder
				.baseUrl(varselProperties.getEndpoints().getDokmetUrl())
				.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.build();
		this.objectMapper = objectMapper;
	}

	@Cacheable(DOKMET_CACHE)
	@Retryable(retryFor = DokmetTechnicalException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
	public Varselinfo hentVarselinfo(final String varseltypeId) {
		log.info("hentVarselinfo henter varselinfo for varseltypeId={}", varseltypeId);

		var varselinfoTo = restClient.get()
				.uri(uriBuilder -> uriBuilder.path("/{varseltypeId}")
						.build(varseltypeId))
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					ProblemDetail problemDetail = objectMapper.readValue(res.getBody(), ProblemDetail.class);
					if (res.getStatusCode().is4xxClientError()) {
						throw new VarselinfoIkkeFunnetException(format("Dokmet feilet funksjonelt med feilmelding=%s. Fant ingen varselinfo med varseltypeId=%s.",
								problemDetail.getDetail(), varseltypeId));
					}
					throw new DokmetTechnicalException(format("Dokmet feilet teknisk for varseltypeId=%s med feilmelding=%s",
							varseltypeId, problemDetail.getDetail()));
				})
				.body(VarselInfoTo.class);

		log.info("hentVarselinfo har hentet varselinfo for varseltypeId={}", varseltypeId);

		return mapToVarselinfo(varselinfoTo);
	}
}