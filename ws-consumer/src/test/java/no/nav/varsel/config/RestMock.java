package no.nav.varsel.config;

import static no.nav.varsel.wsconsumer.dokkat.support.VarselInfoMapperTest.createVarselInfo;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Rest mocks for itest
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class RestMock {

	private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

	private Map<String, ResponseCreator> mocks = new HashMap<>();

	@Bean
	public Map<String, ResponseCreator> restMocks(RestTemplate restTemplate) throws JsonProcessingException {

		// MockRestServiceServer will only mock a request once, do it a bit more manual here
		mocks.put("http://localhost:8041/varsel/rest/varselInfoV1/varseltypeId",
				withSuccess(objectMapper.writeValueAsString(createVarselInfo()), APPLICATION_JSON));
		mocks.put("http://localhost:8041/varsel/rest/varselInfoV1/varsel_test_feil",
				withSuccess(objectMapper.writeValueAsString(createVarselInfo()), APPLICATION_JSON));

		restTemplate.setRequestFactory((uri, httpMethod) -> {
			MockClientHttpRequest mockClientHttpRequest = new MockClientHttpRequest(httpMethod, uri);
			if (mocks.containsKey(uri.toString())) {
				mockClientHttpRequest.setResponse(mocks.get(uri.toString()).createResponse(mockClientHttpRequest));
			} else {
				mockClientHttpRequest.setResponse(withStatus(NOT_FOUND).createResponse(mockClientHttpRequest));
			}
			return mockClientHttpRequest;
		});
		return mocks;
	}
}
