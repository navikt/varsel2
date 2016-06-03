package no.nav.varsel.web.selftest.test;

import no.nav.varsel.domain.to.Ping;
import no.nav.varsel.web.selftest.support.AbstractSelftest;

/**
 * General purpose pinger that uses a {@link Ping} object
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class PingSelftest extends AbstractSelftest {
	private final Runnable pinger;

	public PingSelftest(Ping ping) {
		super(ping.getType(), ping.getName(), ping.getAddress(), ping.getBeskrivelse());
		pinger = ping.getPinger();
	}

	@Override
	protected void doCheck() throws Exception {
		if (pinger != null) {
			pinger.run();
		} else {
			description += " - NB unpingable";
		}
	}
}
