package no.nav.varsel.consumer.dokmet;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokmet.api.tkat021.VarselInfoTo;
import no.nav.varsel.consumer.dokmet.support.VarselinfoMapper;
import no.nav.varsel.consumer.dokmet.to.Varselinfo;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static no.nav.varsel.consumer.config.cache.LokalCacheConfig.VARSELINFO_CACHE;
import static no.nav.varsel.consumer.pdl.helper.DomainConstants.APP_NAME;
import static no.nav.varsel.util.MDCGenerate.CALL_ID;
import static no.nav.varsel.util.MDCGenerate.NAV_CONSUMER_ID;
import static org.springframework.http.HttpMethod.GET;

@Component
@Slf4j
public class DokmetConsumer {

	private final RestTemplate restTemplate;
	private final VarselinfoMapper varselinfoMapper;
	private final String varselinfoUrl;

	public DokmetConsumer(@Value("${dokmet.varselinfo.url}") String varselinfoUrl,
						  RestTemplate restTemplate,
						  VarselinfoMapper varselinfoMapper) {
		this.restTemplate = restTemplate;
		this.varselinfoUrl = varselinfoUrl;
		this.varselinfoMapper = varselinfoMapper;
	}

	@Cacheable(VARSELINFO_CACHE)
	public Varselinfo hentVarselinfo(String varseltypeId) {
		VarselInfoTo varselInfoTo;
		HttpHeaders headers = createHeaders();

		try {
			HttpEntity<String> request = new HttpEntity<>(headers);
			varselInfoTo = restTemplate.exchange(varselinfoUrl + "/{varseltypeId}", GET, request, VarselInfoTo.class, varseltypeId).getBody();
		} catch (Exception e) {
			throw new RuntimeException("Could not find varseltypeId=" + varseltypeId + " from url=" + varselinfoUrl, e);
		}

		return varselinfoMapper.map(varselInfoTo);
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(NAV_CONSUMER_ID, APP_NAME);
		headers.add(CALL_ID, MDC.get(CALL_ID));
		return headers;
	}
}
