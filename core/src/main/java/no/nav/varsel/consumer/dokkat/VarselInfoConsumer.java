package no.nav.varsel.consumer.dokkat;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.varsel.azure.TokenConsumer;
import no.nav.varsel.azure.TokenResponse;
import no.nav.varsel.azure.AzureProperties;
import no.nav.varsel.consumer.dokkat.support.VarselInfoMapper;
import no.nav.varsel.consumer.dokkat.to.VarselInfoTo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import static no.nav.varsel.consumer.pdl.helper.DomainConstants.APP_NAME;
import static no.nav.varsel.util.MDCGenerate.CALL_ID;
import static no.nav.varsel.util.MDCGenerate.NAV_CONSUMER_ID;
import static org.springframework.http.HttpMethod.GET;

@Component
@Slf4j
public class VarselInfoConsumer {

	private final RestTemplate restTemplate;
	private final VarselInfoMapper varselInfoMapper;
	private final String varselinfoUrl;

	public VarselInfoConsumer(@Value("${dokmet.varselinfo.url}") String varselinfoUrl,
							  RestTemplate restTemplate,
							  VarselInfoMapper varselInfoMapper) {
		this.restTemplate = restTemplate;
		this.varselinfoUrl = varselinfoUrl;
		this.varselInfoMapper = varselInfoMapper;
	}

	public VarselInfoTo hentVarselInfo(String varseltypeId) {
		VarselInfoRestTo varselInfo;
		HttpHeaders headers = createHeaders();
		String url = varselinfoUrl + "/" + varseltypeId;

		try {
			HttpEntity<String> request = new HttpEntity<>(headers);
			varselInfo = restTemplate.exchange(url, GET, request, VarselInfoRestTo.class).getBody();
		} catch (Exception e) {
			throw new RuntimeException("Could not find varseltypeId=" + varseltypeId + " from url=" + url, e);
		}
		return varselInfoMapper.map(varselInfo);
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(NAV_CONSUMER_ID, APP_NAME);
		headers.add(CALL_ID, MDC.get(CALL_ID));
		return headers;
	}
}
