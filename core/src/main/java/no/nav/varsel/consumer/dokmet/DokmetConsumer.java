package no.nav.varsel.consumer.dokmet;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokmet.api.tkat021.VarselInfoTo;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.util.NavHeadersFilter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Consumer;

import static java.lang.String.format;
import static no.nav.varsel.consumer.config.cache.LokalCacheConfig.DOKMET_CACHE;
import static no.nav.varsel.consumer.dokmet.VarselinfoMapper.mapToVarselinfo;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class DokmetConsumer {

	private final WebClient webClient;

	public DokmetConsumer(VarselProperties varselProperties,
						  WebClient webClient) {
		this.webClient = webClient.mutate()
				.baseUrl(varselProperties.getEndpoints().getDokmetUrl())
				.filter(new NavHeadersFilter())
				.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.build();
	}

	@Cacheable(DOKMET_CACHE)
	@Retryable(retryFor = DokmetTechnicalException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
	public Varselinfo hentVarselinfo(final String varseltypeId) {
		log.info("hentVarselinfo henter varselinfo for varseltypeId={}", varseltypeId);

		var varselinfoTo = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/{varseltypeId}")
						.build(varseltypeId))
				.retrieve()
				.bodyToMono(VarselInfoTo.class)
				.doOnError(handleError(varseltypeId))
				.block();

		log.info("hentVarselinfo har hentet varselinfo for varseltypeId={}", varseltypeId);

		return mapToVarselinfo(varselinfoTo);
	}

	private Consumer<Throwable> handleError(String varseltypeId) {
		return error -> {
			if (error instanceof WebClientResponseException response && response.getStatusCode().is4xxClientError()) {
				if (response.getStatusCode().isSameCodeAs(NOT_FOUND)) {
					throw new VarselinfoIkkeFunnetException(format("Dokmet feilet funksjonelt med statuskode=%s. Fant ingen varselinfo med varseltypeId=%s.",
							response.getStatusCode(), varseltypeId), error);
				}

				throw new DokmetFunctionalException(format("Dokmet feilet funksjonelt med statuskode=%s. Kunne ikke hente varselinfo med varseltypeId=%s.",
						response.getStatusCode(), varseltypeId), error);
			} else {
				throw new DokmetTechnicalException(format("Dokmet feilet teknisk for varseltypeId=%s med feilmelding=%s",
						varseltypeId, error.getMessage()), error
				);
			}
		};
	}

}