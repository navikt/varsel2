package no.nav.varsel.util;

import org.slf4j.MDC;

import java.util.Set;
import java.util.UUID;

public class MDCGenerate {

	public static final String CALL_ID = "callId";
	public static final String USER_ID = "userId";

	public static Set<String> ALL_KEYS = Set.of(CALL_ID);

	public static void generateCallId() {
		MDC.put(CALL_ID, UUID.randomUUID().toString());
	}

	public static void clearCallId() {
		if (MDC.get(CALL_ID) != null) {
			MDC.remove(CALL_ID);
		}
	}

	public static void setUserId(String userId) {
		MDC.put(USER_ID, userId);
	}

	public static void clearUserId() {
		if (MDC.get(USER_ID) != null) {
			MDC.remove(USER_ID);
		}
	}

	public static String getCallId() {
		return MDC.get(CALL_ID);
	}
}
