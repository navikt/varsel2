package no.nav.varsel.consumer.sts;


import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.consumer.sts.support.StsTechnicalException;
import no.nav.varsel.consumer.sts.support.TechnicalVarselException;
import no.nav.varsel.consumer.sts.to.StsResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static no.nav.varsel.consumer.config.cache.LokalCacheConfig.STS_CACHE;

@Component
public class StsRestConsumer {

	private final RestTemplate restTemplate;
	private final String stsUrl;

	public static final int DELAY = 500;
	public static final int MULTIPLIER = 2;

	@Autowired
	public StsRestConsumer(
			RestTemplateBuilder restTemplate,
			VarselProperties varselProperties
	) {
		this.stsUrl = varselProperties.getEndpoints().getStsUrl();
		this.restTemplate = restTemplate
				.connectTimeout(Duration.ofSeconds(5))
				.basicAuthentication(varselProperties.getServiceuser().getUsername(), varselProperties.getServiceuser().getPassword())
				.build();
	}

	@Retryable(retryFor = TechnicalVarselException.class, backoff = @Backoff(delay = DELAY, multiplier = MULTIPLIER))
	@Cacheable(STS_CACHE)
	public String getOidcToken() {
		try {
			return restTemplate
					.getForObject(stsUrl + "?grant_type=client_credentials&scope=openid", StsResponseTo.class)
					.getAccessToken();
		} catch (HttpStatusCodeException e) {
			throw new StsTechnicalException(String.format("Kall mot STS feilet med status=%s feilmelding=%s.", e.getStatusCode(), e
					.getMessage()), e);
		}
	}
}
