package no.nav.varsel.web;

import static java.util.stream.Collectors.toList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import no.nav.varsel.config.BatchConfig;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Itest for BatchController
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BatchControllerTest extends AbstractRestTest {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void shouldBeAbleToStartBatch() throws Exception {
		HashMap<String, String> parameters = new HashMap<>();
		String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(BatchConfig.START_TIME_FORMAT));
		parameters.put("startTime", startTime);
		parameters.put("workUnit", "2");

		String parameterString = Joiner.on(",").join(parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + e.getValue()).collect(toList()));

		HashMap<String, String> var = new HashMap<>();
		var.put("jobParameters", parameterString);
		byte[] content = objectMapper.writeValueAsBytes(var);
		mockMvc.perform(post("/batch/launch/BVARSEL001")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldPing() throws Exception {
		mockMvc.perform(get("/batch/ping")).andExpect(status().isOk());
	}
}
