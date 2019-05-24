package no.nav.varsel.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Component
public class RetryLoggingInterceptor extends RetryListenerSupport {
	private static final Logger LOG = LoggerFactory.getLogger(RetryLoggingInterceptor.class);

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
		super.onError(context, callback, throwable);
		LOG.warn(String.format("Retry trigget for %s. gang med feilmelding=%s ", context.getRetryCount(), throwable.getMessage()), throwable);
	}

}
