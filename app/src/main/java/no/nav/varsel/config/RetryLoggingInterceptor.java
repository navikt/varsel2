package no.nav.varsel.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryLoggingInterceptor implements RetryListener {

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
		// Logg retry uten stacktrace
		log.info("Retry trigget for {}. gang med feilmelding={} ", context.getRetryCount(), throwable.getMessage());
	}
}
