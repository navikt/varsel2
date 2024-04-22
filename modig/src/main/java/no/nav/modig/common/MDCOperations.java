package no.nav.modig.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.security.SecureRandom;

import static java.lang.Integer.MAX_VALUE;

/**
 * Utility-klasse for kommunikasjon med MDC.
 */
public final class MDCOperations {

	private static final Logger log = LoggerFactory.getLogger(MDCOperations.class.getName());

	public static final String MDC_CALL_ID = "callId";
	public static final String MDC_USER_ID = "userId";
	public static final String MDC_CONSUMER_ID = "consumerId";

	private static final SecureRandom RANDOM = new SecureRandom();

	private MDCOperations() {
	}

	public static String generateCallId() {
		int randomNr = getRandomNumber();
		long systemTime = getSystemTime();

		String callId = "CallId_" +
						systemTime +
						"_" +
						randomNr;

		return callId;
	}

	public static void putToMDC(String key, String value) {
		log.debug("Putting value: " + value + " on MDC with key: " + key);
		MDC.put(key, value);
	}

	public static void remove(String key) {
		log.debug("Removing key: " + key);
		MDC.remove(key);
	}

	private static int getRandomNumber() {
		return RANDOM.nextInt(MAX_VALUE);
	}

	private static long getSystemTime() {
		return System.currentTimeMillis();
	}
}
