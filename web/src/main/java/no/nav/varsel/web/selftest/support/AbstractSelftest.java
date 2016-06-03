package no.nav.varsel.web.selftest.support;

import com.google.common.base.Throwables;
import no.nav.varsel.domain.to.Ping;
import org.springframework.util.StopWatch;

/**
 * Abstract selftest that produces {@link SelftestCheck}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public abstract class AbstractSelftest {

	protected String name;
	protected String description;
	protected String address;
	protected Ping.Type type;

	public AbstractSelftest(Ping.Type type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public AbstractSelftest(Ping.Type type, String name, String address, String description) {
		this.type = type;
		this.name = name;
		this.address = address;
		this.description = description;
	}

	protected abstract void doCheck() throws Exception;

	protected Ping.Type getType() {
		return type;
	}

	protected String getName() {
		return name;
	}

	protected String getAddress() {
		return address;
	}

	protected String getDescription() {
		return description;
	}

	/**
	 * Override to false if test should return warning on failure
	 */
	protected boolean isVital() {
		return true;
	}

	public SelftestCheck check() {
		SelftestCheck check = new SelftestCheck();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try {
			doCheck();
		} catch (Exception e) {
			@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
			Throwable rootCause = Throwables.getRootCause(e);
			if (rootCause != null) {
				check.setErrorMessage(rootCause.getMessage());
			} else {
				check.setErrorMessage(e.getMessage());
			}
			check.setStackTrace(Throwables.getStackTraceAsString(e));
			check.setResult(isVital() ? Result.ERROR : Result.WARNING);
		}
		stopWatch.stop();
		check.setResponseTime(stopWatch.getTotalTimeMillis());
		check.setDescription(getDescription());
		check.setEndpoint(getName());
		check.setAddress(getAddress());
		check.setType(getType());
		return check;
	}
}
