package no.nav.varsel.consumer.pdl;

import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.pdl.support.PdlFunctionalException;
import no.nav.varsel.consumer.pdl.support.PersonIkkeFunnetException;
import no.nav.varsel.consumer.pdl.support.ServerErrorException;
import no.nav.varsel.consumer.pdl.to.PdlRequest;
import no.nav.varsel.consumer.pdl.to.PdlResponse;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;

import static java.util.Objects.requireNonNull;
import static no.nav.varsel.consumer.pdl.helper.DomainConstants.BEARER_PREFIX;
import static no.nav.varsel.util.MDCGenerate.getCallId;
import static no.nav.varsel.util.NavConstants.NAV_CALL_ID;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class PdlIdentConsumer {
	private static final String PERSON_IKKE_FUNNET_CODE = "not_found";
	private static final String SERVER_ERROR_CODE = "server_error";

	private final RestTemplate restTemplate;
	private final StsRestConsumer stsRestConsumer;
	private final URI pdlUri;

	public static final int DELAY = 500;
	public static final int MULTIPLIER = 2;

	@Autowired
	public PdlIdentConsumer(
			VarselProperties varselProperties,
			RestTemplateBuilder restTemplateBuilder,
			StsRestConsumer stsRestConsumer,
			ClientHttpRequestFactory clientHttpRequestFactory
	) {
		this.restTemplate = restTemplateBuilder
				.connectTimeout(Duration.ofSeconds(5))
				.requestFactory(() -> clientHttpRequestFactory)
				.build();
		this.stsRestConsumer = stsRestConsumer;
		this.pdlUri = UriComponentsBuilder.fromUriString(varselProperties.getEndpoints().getPdlUrl()).build().toUri();
	}

	@Retryable(
			retryFor = HttpServerErrorException.class,
			backoff = @Backoff(delay = DELAY, multiplier = MULTIPLIER)
	)
	public String hentAktoerId(final String folkeregisterIdent) throws PersonIkkeFunnetException {
		if (isBlank(folkeregisterIdent)) {
			throw new PersonIkkeFunnetException("Folkeregisterident er null eller blank.");
		}
		return baseHentIdent(mapHentAktoerIdForFolkeregisterident(folkeregisterIdent));
	}

	@Retryable(
			retryFor = HttpServerErrorException.class,
			backoff = @Backoff(delay = DELAY, multiplier = MULTIPLIER)
	)
	public String hentFolkeregisterIdent(final String aktoerId) throws PersonIkkeFunnetException {
		if (isBlank(aktoerId)) {
			throw new PersonIkkeFunnetException("AktoerId er null eller blank.");
		}
		return baseHentIdent(mapHentFolkeregisterIdentForAktoerId(aktoerId));
	}

	public String baseHentIdent(final PdlRequest query) {
		try {
			final RequestEntity<PdlRequest> requestEntity = baseRequest().body(query);
			final PdlResponse pdlResponse = requireNonNull(restTemplate.exchange(requestEntity, PdlResponse.class).getBody());

			if (pdlResponse.getErrors() == null || pdlResponse.getErrors().isEmpty()) {
				return pdlResponse.getData().getHentIdenter().getIdenter().get(0).getIdent();
			} else if(pdlResponse.getErrors().stream().anyMatch(e -> SERVER_ERROR_CODE.equals(e.getExtensions().getCode()))) {
				throw new ServerErrorException("Server_error hindrer aktørid i å bli hentet fra pdl");
			} else if (PERSON_IKKE_FUNNET_CODE.equals(pdlResponse.getErrors().get(0).getExtensions().getCode())) {
					throw new PersonIkkeFunnetException("Fant ikke aktørid for person i pdl.");
			}
			throw new ServerErrorException("Kunne ikke hente aktørid for folkeregisterident i pdl. " + pdlResponse.getErrors());

		} catch (HttpClientErrorException e) {
			throw new PdlFunctionalException("Kall mot pdl feilet funksjonelt.", e);
		}
	}

	private PdlRequest mapHentAktoerIdForFolkeregisterident(final String ident) {
		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("ident", ident);
		return PdlRequest.builder()
				.query("query hentIdenter($ident: ID!) {hentIdenter(ident: $ident, grupper: AKTORID, historikk: false) {identer { ident gruppe historisk } } }")
				.variables(variables)
				.build();
	}

	private PdlRequest mapHentFolkeregisterIdentForAktoerId(final String ident) {
		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("ident", ident);
		return PdlRequest.builder()
				.query("query hentIdenter($ident: ID!) {hentIdenter(ident: $ident, grupper: FOLKEREGISTERIDENT, historikk: false) {identer { ident gruppe historisk } } }")
				.variables(variables)
				.build();
	}

	private RequestEntity.BodyBuilder baseRequest() {
		final String serviceuserToken = stsRestConsumer.getOidcToken();
		// hentIdenter query krever ikke "behandlingsnummer" header
		return RequestEntity.post(pdlUri)
				.accept(APPLICATION_JSON)
				.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.header(AUTHORIZATION, BEARER_PREFIX + serviceuserToken)
				.header(NAV_CALL_ID, getCallId());
	}
}
