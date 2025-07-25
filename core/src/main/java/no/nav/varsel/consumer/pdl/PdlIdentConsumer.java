package no.nav.varsel.consumer.pdl;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.pdl.support.PersonIkkeFunnetException;
import no.nav.varsel.consumer.pdl.to.PdlRequest;
import no.nav.varsel.consumer.pdl.to.PdlResponse;
import no.nav.varsel.exception.functional.PdlHentIdentFunctionalException;
import no.nav.varsel.exception.technical.PdlHentIdentTechnicalException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;

import static java.lang.String.format;
import static no.nav.varsel.consumer.naistoken.NaisTexasRequestInterceptor.TARGET_SCOPE;
import static no.nav.varsel.consumer.pdl.to.PdlResponse.PdlGruppe.AKTORID;
import static no.nav.varsel.consumer.pdl.to.PdlResponse.PdlGruppe.FOLKEREGISTERIDENT;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class PdlIdentConsumer {

	private static final String PERSON_IKKE_FUNNET_CODE = "not_found";
	private final ObjectMapper objectMapper;

	EnumSet<PdlResponse.PdlGruppe> PDL_GRUPPE = EnumSet.of(FOLKEREGISTERIDENT, AKTORID);

	// https://pdldocs-navno.msappproxy.net/ekstern/index.html#_dokumenter_hjemmel
	private static final String HEADER_PDL_BEHANDLINGSNUMMER = "behandlingsnummer";

	// https://behandlingskatalog.nais.adeo.no/process/purpose/ARKIVPLEIE/756fd557-b95e-4b20-9de9-6179fb8317e6
	private static final String ARKIVPLEIE_BEHANDLINGSNUMMER = "B315";

	private final RestClient restClientTexas;
	private final String pdlScope;

	public PdlIdentConsumer(VarselProperties varselProperties,
							RestClient restClientTexas,
							ObjectMapper objectMapper) {
		this.restClientTexas = restClientTexas
				.mutate()
				.baseUrl(varselProperties.getEndpoints().getPdl().getUrl())
				.defaultHeaders(httpHeader -> {
					httpHeader.setContentType(APPLICATION_JSON);
					httpHeader.set(HEADER_PDL_BEHANDLINGSNUMMER, ARKIVPLEIE_BEHANDLINGSNUMMER);
				})
				.build();
		this.pdlScope = varselProperties.getEndpoints().getPdl().getScope();
		this.objectMapper = objectMapper;
	}

	@Retryable(retryFor = PdlHentIdentTechnicalException.class)
	public String hentAktoerId(final String folkeregisterIdent) throws PersonIkkeFunnetException {
		if (isBlank(folkeregisterIdent)) {
			throw new PersonIkkeFunnetException("Folkeregisterident er null eller blank.");
		}
		return baseHentIdent(mapHentAktoerIdForFolkeregisterident(folkeregisterIdent), "aktoerid");
	}

	@Retryable(retryFor = PdlHentIdentTechnicalException.class)
	public String hentFolkeregisterIdent(final String aktoerId) throws PersonIkkeFunnetException {
		if (isBlank(aktoerId)) {
			throw new PersonIkkeFunnetException("AktoerId er null eller blank.");
		}
		return baseHentIdent(mapHentFolkeregisterIdentForAktoerId(aktoerId), "folkeregisterident");
	}

	public String baseHentIdent(final PdlRequest query, String identGruppe) {
		final PdlResponse pdlResponse = restClientTexas.post()
				.attribute(TARGET_SCOPE, pdlScope)
				.body(query)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					ProblemDetail problemDetail = objectMapper.readValue(res.getBody(), ProblemDetail.class);
					if (res.getStatusCode().is4xxClientError()) {
						throw new PersonIkkeFunnetException(format("PDL feilet funksjonelt med feilmelding=%s. Fant ingen %s.",
								problemDetail.getDetail(), identGruppe));
					}
					throw new PdlHentIdentTechnicalException("Kunne ikke hente aktørid eller folkeregisterident fra pdl. " + problemDetail.getDetail());

				})
				.body(PdlResponse.class);

		if (isEmpty(pdlResponse.getErrors())) {
			return getIdentFromResponse(pdlResponse, identGruppe);
		} else {
			if (PERSON_IKKE_FUNNET_CODE.equals(pdlResponse.getErrors().get(0).getExtensions().getCode())) {
				throw new PersonIkkeFunnetException("Fant ikke ident for person i PDL.");
			}
			throw new PdlHentIdentTechnicalException("Kunne ikke hente aktørid eller folkeregisterident fra pdl. " + pdlResponse.getErrors());
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

	private String getIdentFromResponse(PdlResponse pdlResponse, String tjenesete) {
		return Optional.ofNullable(pdlResponse.getData())
				.map(PdlResponse.PdlHentIdenter::getHentIdenter)
				.map(PdlResponse.PdlIdenter::getIdenter)
				.flatMap(identer -> identer.stream()
						.filter(it -> PDL_GRUPPE.contains(it.getGruppe()))
						.filter(it -> !it.isHistorisk())
						.map(PdlResponse.PdlIdent::getIdent)
						.findFirst())
				.orElseThrow(() -> new PdlHentIdentFunctionalException(format("Kunne ikke hente %s fra PDL. Respons fra PDL inneholdt ikke gjeldende %s", tjenesete, tjenesete)));
	}
}
