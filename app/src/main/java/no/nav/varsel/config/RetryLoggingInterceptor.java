package no.nav.varsel.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.retry.RetryException;
import org.springframework.resilience.retry.MethodRetryEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryLoggingInterceptor {

	@EventListener
	public void onRetry(MethodRetryEvent event) {
		Throwable cause = (event.getFailure() instanceof RetryException re) ? re.getCause() : event.getFailure();
		log.info("Retry trigget for {} med feilmelding={}", event.getMethod().getName(), cause.getMessage());
	}
}
