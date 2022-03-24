package no.nav.varsel.web.selftest;

import no.nav.varsel.config.AppConfig;
import no.nav.varsel.config.WsConsumerTestConfig;
import no.nav.varsel.web.AbstractRestTest;
import org.junit.Test;
import org.springframework.context.annotation.Import;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static no.nav.varsel.web.selftest.SelftestController.APPLICATION_UP;
import static no.nav.varsel.web.selftest.support.Result.ERROR;
import static no.nav.varsel.web.selftest.support.Result.OK;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({AppConfig.class, WsConsumerTestConfig.class})
public class SelftestControllerTest extends AbstractRestTest {

	@Test
	public void shouldSelftestThymeleaf() throws Exception {
		mockMvc.perform(get("/internal/selftest").accept(TEXT_HTML))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldGiveErrorCodeIfStatusFlagSet() throws Exception {
		mockMvc.perform(get("/internal/selftest?status=1").accept(TEXT_HTML))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void shouldSelftestJsonAndNotGiveErrorCode() throws Exception {
		mockMvc.perform(get("/internal/selftest").accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.application", is("varsel")))
				.andExpect(jsonPath("$.version", notNullValue()))
				.andExpect(jsonPath("$.node", notNullValue()))
				.andExpect(jsonPath("$.error", is(true)))
				.andExpect(jsonPath("$.aggregateResponseTime", notNullValue()))
				.andExpect(jsonPath("$.aggregateResult", is(ERROR.auraCode)))
				.andExpect(jsonPath("$.aggregateResultText", is(ERROR.name())))
				.andExpect(jsonPath("$.timestamp", notNullValue()))

				.andExpect(jsonPath("$.checks", hasSize(3)))
				.andExpect(jsonPath("$.checks[0].type", is("Datasource")))
				.andExpect(jsonPath("$.checks[0].endpoint", is("varselDS")))
				.andExpect(jsonPath("$.checks[0].address", nullValue()))
				.andExpect(jsonPath("$.checks[0].description", is("Varsel Oracle Database")))
				.andExpect(jsonPath("$.checks[0].errorMessage", nullValue()))
				.andExpect(jsonPath("$.checks[0].stackTrace", nullValue()))
				.andExpect(jsonPath("$.checks[0].resultText", is(OK.name())))
				.andExpect(jsonPath("$.checks[0].result", is(OK.auraCode)))
				.andExpect(jsonPath("$.checks[0].responseTime", notNullValue()));

	}

	@Test
	public void shouldBeAlive() throws Exception {
		mockMvc.perform(get("/internal/isAlive"))
				.andExpect(status().isOk())
				.andExpect(content().string(APPLICATION_UP));
	}

	private String getServerAddress() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			return "N/A";
		}
	}
}