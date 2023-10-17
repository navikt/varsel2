package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.dokkat.support.VarselInfoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static java.util.Base64.getEncoder;
import static java.util.Collections.singletonList;

@Configuration
public class RestConsumerConfig {

	public static final int TIMEOUT = 30_000;

	@Value("${varsel.serviceuser.username}")
	private String srvVarselUsername;
	@Value("${varsel.serviceuser.password}")
	private String srvVarselPassword;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(requestFactory());
	}

	protected InterceptingClientHttpRequestFactory requestFactory() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(TIMEOUT);
		return new InterceptingClientHttpRequestFactory(requestFactory, singletonList(basicAuthInterceptor()));
	}

	protected ClientHttpRequestInterceptor basicAuthInterceptor() {
		return (request, body, execution) -> {
			String token = getEncoder().encodeToString((srvVarselUsername + ":" + srvVarselPassword).getBytes());
			request.getHeaders().add("Authorization", "Basic " + token);
			return execution.execute(request, body);
		};
	}

	@Bean
	public VarselInfoMapper varselInfoMapper() {
		return new VarselInfoMapper();
	}

}
