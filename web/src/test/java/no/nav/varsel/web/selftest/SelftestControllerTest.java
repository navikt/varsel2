package no.nav.varsel.web.selftest;

import static no.nav.varsel.web.selftest.SelftestController.APPLICATION_UP;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.nav.varsel.config.AppConfig;
import no.nav.varsel.config.WsConsumerTestConfig;
import no.nav.varsel.web.AbstractRestTest;
import no.nav.varsel.web.selftest.support.Result;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * Itest for {@link SelftestController}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@SpringApplicationConfiguration(classes = {AppConfig.class, WsConsumerTestConfig.class})
public class SelftestControllerTest extends AbstractRestTest {


	@Test
	public void shouldSelftestThymeleaf() throws Exception {
		mockMvc.perform(get("/internal/selftest").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldGiveErrorCodeIfStatusFlagSet() throws Exception {
		mockMvc.perform(get("/internal/selftest?status=1").accept(MediaType.TEXT_HTML))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void shouldSelftestJsonAndGiveErrorCode() throws Exception {
		mockMvc.perform(get("/internal/selftest").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.application", is("varsel")))
				.andExpect(jsonPath("$.version", notNullValue()))
				.andExpect(jsonPath("$.node", notNullValue()))
				.andExpect(jsonPath("$.error", is(true)))
				.andExpect(jsonPath("$.aggregateResponseTime", notNullValue()))
				.andExpect(jsonPath("$.aggregateResult", is(Result.ERROR.auraCode)))
				.andExpect(jsonPath("$.aggregateResultText", is(Result.ERROR.name())))
				.andExpect(jsonPath("$.timestamp", notNullValue()))

				.andExpect(jsonPath("$.checks", hasSize(11)))
				.andExpect(jsonPath("$.checks[0].type", is("Datasource")))
				.andExpect(jsonPath("$.checks[0].endpoint", is("varselDS")))
				.andExpect(jsonPath("$.checks[0].address", nullValue()))
				.andExpect(jsonPath("$.checks[0].description", is("Varsel Oracle Database")))
				.andExpect(jsonPath("$.checks[0].errorMessage", nullValue()))
				.andExpect(jsonPath("$.checks[0].stackTrace", nullValue()))
				.andExpect(jsonPath("$.checks[0].resultText", is(Result.OK.name())))
				.andExpect(jsonPath("$.checks[0].result", is(Result.OK.auraCode)))
				.andExpect(jsonPath("$.checks[0].responseTime", notNullValue()))

				.andExpect(jsonPath("$.checks[1].type", is("Rest")))
				.andExpect(jsonPath("$.checks[1].endpoint", is("Batch")))
				.andExpect(jsonPath("$.checks[1].address", is("http://localhost:8080/varsel/batch/ping")))
				.andExpect(jsonPath("$.checks[1].errorMessage", notNullValue()))
				.andExpect(jsonPath("$.checks[1].stackTrace", notNullValue()))
				.andExpect(jsonPath("$.checks[1].resultText", is(Result.ERROR.name())))
				.andExpect(jsonPath("$.checks[1].result", is(Result.ERROR.auraCode)));
	}

	@Test
	public void shouldBeAlive() throws Exception {
		mockMvc.perform(get("/internal/isAlive"))
				.andExpect(status().isOk())
				.andExpect(content().string(APPLICATION_UP));
	}
}