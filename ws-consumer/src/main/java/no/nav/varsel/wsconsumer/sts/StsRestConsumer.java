package no.nav.varsel.wsconsumer.sts;


import no.nav.varsel.wsconsumer.sts.support.StsTechnicalException;
import no.nav.varsel.wsconsumer.sts.support.TechnicalVarselException;
import no.nav.varsel.wsconsumer.sts.to.StsResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import java.time.Duration;

import static no.nav.varsel.config.cache.LokalCacheConfig.STS_CACHE;

@Component
public class StsRestConsumer {

	private final RestTemplate restTemplate;
	private final String stsUrl;

	public static final int DELAY = 500;
	public static final int MULTIPLIER = 2;

	@Inject
	public StsRestConsumer(
			RestTemplateBuilder restTemplate,
			@Value("${securityTokenRestService.url}") String stsUrl,
			@Value("${no.nav.modig.security.systemuser.username}") String serviceuserUsername,
			@Value("${no.nav.modig.security.systemuser.password}") String serviceuserPassword
	) {
		this.stsUrl = stsUrl;
		this.restTemplate = restTemplate
				.setConnectTimeout(Duration.ofSeconds(5))
				.setReadTimeout(Duration.ofSeconds(20))
				.basicAuthentication(serviceuserUsername, serviceuserPassword)
				.build();
	}

	@Retryable(include = TechnicalVarselException.class, backoff = @Backoff(delay = DELAY, multiplier = MULTIPLIER))
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
