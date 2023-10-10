package no.nav.varsel.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.modig.common.MDCOperations;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MDCVarselFilter extends OncePerRequestFilter {

	public static final String MDC_USER_ID = "userId";
	public static final String MDC_CONSUMER_ID = "consumerId";

	public MDCVarselFilter() {
	}

	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
	}

	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		log.debug("Entering filter to extract values and put on MDC for logging");
		String userId = MDC.get(MDC_USER_ID) != null ? MDC.get(MDC_CONSUMER_ID) : "";
		String consumerId = MDC.get(MDC_CONSUMER_ID) != null ? MDC.get(MDC_CONSUMER_ID) : "";
		String callId = MDCOperations.generateCallId();
		MDCOperations.putToMDC("callId", callId);
		MDCOperations.putToMDC("userId", userId);
		MDCOperations.putToMDC("consumerId", consumerId);
		log.debug("Values added");
		try {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} finally {
			MDCOperations.remove("callId");
			MDCOperations.remove("userId");
			MDCOperations.remove("consumerId");
			log.debug("Cleared MDC session");
		}

	}
}
