package no.nav.varsel.consumer.naistoken;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Map;

import static no.nav.varsel.util.MDCGenerate.getCallId;
import static no.nav.varsel.util.NavConstants.NAV_CALL_ID;

public class NaisTexasRequestInterceptor implements ClientHttpRequestInterceptor {

	public static final String TARGET_SCOPE = "targetScope";

	private final NaisTexasConsumer naisTexasConsumer;

	public NaisTexasRequestInterceptor(NaisTexasConsumer naisTexasConsumer) {
		this.naisTexasConsumer = naisTexasConsumer;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

		Map<String, Object> attributes = request.getAttributes();
		if (!attributes.containsKey(TARGET_SCOPE)) {
			throw new IllegalArgumentException("Kan ikke bruke denne restClient uten at targetScope attributtet er satt");
		}
		String targetScope = (String) attributes.get(TARGET_SCOPE);
		String token = naisTexasConsumer.getSystemToken(targetScope);
		if (token != null) {
			request.getHeaders().setBearerAuth(token);
			request.getHeaders().set(NAV_CALL_ID, getCallId());
			return execution.execute(request, body);
		} else {
			throw new IllegalStateException("Kunne ikke hente token for targetScope: " + targetScope);
		}
	}
}
