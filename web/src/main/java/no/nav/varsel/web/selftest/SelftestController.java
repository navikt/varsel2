package no.nav.varsel.web.selftest;

import no.nav.varsel.jms.JmsPingProvider;
import no.nav.varsel.web.selftest.support.SelftestResponse;
import no.nav.varsel.web.selftest.test.DbSelftest;
import no.nav.varsel.web.selftest.test.HttpSelftest;
import no.nav.varsel.web.selftest.test.PingSelftest;
import no.nav.varsel.wsconsumer.WsPingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Controller for selftest
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Controller
public class SelftestController {

	// Application is up and running
	public static final String APPLICATION_UP = "application: UP";
	// Application is up but refusing connections
	public static final String APPLICATION_DOWN = "application: DOWN";

	@Value("${applicationName}")
	private String applicationName;
	@Value("${applicationVersion}")
	private String applicationVersion;
	@Value("${bootstrapVersion}")
	private String bootstrapVersion;

	@Inject
	private DbSelftest dbSelftest;
	@Inject
	private JmsPingProvider jmsPingProvider;
	@Inject
	private WsPingProvider wsPingProvider;

	/**
	 * Thymeleaf view
	 */
	@RequestMapping(value = "/internal/selftest", produces = MediaType.ALL_VALUE)
	public String selftest(@RequestParam(value = "status", required = false) String status,
						   HttpServletResponse httpServletResponse, Model model) throws IOException {
		SelftestResponse response = performSelftest();

		if (status != null && response.isError()) {
			httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		model.addAttribute("selftest", response);
		model.addAttribute("bootstrapVersion", bootstrapVersion);
		return "selftest";
	}

	/**
	 * Rest JSON view
	 */
	@ResponseBody
	@RequestMapping(value = "/internal/selftest", produces = MediaType.APPLICATION_JSON_VALUE)
	public SelftestResponse selftest(HttpServletResponse httpServletResponse) {
		SelftestResponse response = performSelftest();
		if (response.isError()) {
			httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * Rest JSON view
	 */
	@ResponseBody
	@RequestMapping(value = "/internal/isAlive", produces = MediaType.TEXT_PLAIN_VALUE)
	public String isAlive() {
		return APPLICATION_UP;
	}

	private SelftestResponse performSelftest() {
		SelftestResponse response = new SelftestResponse();
		response.setApplication(applicationName);
		response.setVersion(applicationVersion);
		response.setNode(getServerAddress());
		addChecks(response);
		return response;
	}

	private void addChecks(SelftestResponse response) {
		// Datasource
		response.addCheck(dbSelftest.check());

		// Self
		response.addCheck(new HttpSelftest("Batch", "Internal Batch Controller", "http://localhost:8080/varsel/batch/ping").check());

		// Webservice
		response.addCheck(new PingSelftest(wsPingProvider.pingAktoerV2()).check());
		response.addCheck(new PingSelftest(wsPingProvider.pingDigitalKontaktinformasjonV1()).check());
		response.addCheck(new PingSelftest(wsPingProvider.pingVarselInfoV1()).check());
		response.addCheck(new PingSelftest(wsPingProvider.pingKodeverkPortType()).check());

		// Jms
		jmsPingProvider.ping().forEach(p -> response.addCheck(new PingSelftest(p).check()));
	}

	private String getServerAddress() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			return "N/A";
		}
	}
}
