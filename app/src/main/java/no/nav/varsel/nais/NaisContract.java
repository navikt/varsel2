package no.nav.varsel.nais;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Slf4j
@RestController
@Unprotected
public final class NaisContract {

	private static final String APPLICATION_ALIVE = "Application is alive!";
	private static final String APPLICATION_READY = "Application is ready for traffic!";

	@Autowired
	public NaisContract() {
		//TODO: Legg på meterregistry
		//Gauge.builder("dok_app_is_ready", isReady, AtomicInteger::get).register(meterRegistry);
	}

	@GetMapping("/isAlive")
	public String isAlive() {
		return APPLICATION_ALIVE;
	}

	@ResponseBody
	@RequestMapping(value = "/isReady", produces = TEXT_HTML_VALUE)
	public ResponseEntity<String> isReady() {
		return new ResponseEntity<>(APPLICATION_READY, OK);
	}
}