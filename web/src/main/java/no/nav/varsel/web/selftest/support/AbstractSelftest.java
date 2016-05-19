package no.nav.varsel.web.selftest.support;

import com.google.common.base.Throwables;
import org.springframework.util.StopWatch;

/**
 * Abstract selftest that produces {@link SelftestCheck}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public abstract class AbstractSelftest {

	private String name;
	private String description;

	public AbstractSelftest(String name, String description) {
		this.name = name;
		this.description = description;
	}

	protected abstract void doCheck() throws Exception;

	protected String getName() {
		return name;
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
		return check;
	}
}
