package no.nav.varsel.web.selftest.test;

import no.nav.varsel.domain.to.Ping;
import no.nav.varsel.web.selftest.support.AbstractSelftest;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Selftest for http resources
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HttpSelftest extends AbstractSelftest {

	private final String url;

	public HttpSelftest(String name, String description, String url) {
		super(Ping.Type.Rest, name, description);
		this.url = url;
	}

	@Override
	protected void doCheck() throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		int responseCode = connection.getResponseCode();
		Assert.isTrue(responseCode == HttpStatus.OK.value(), String.format("%s failed with errorcode %d", url, responseCode));
	}
}
