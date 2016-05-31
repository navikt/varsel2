package no.nav.varsel.config.endpoint;

import org.apache.cxf.clustering.FailoverFeature;
import org.apache.cxf.clustering.RetryStrategy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;

import java.util.HashMap;

/**
 * Abstract helper class for Cxf Endpoints
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public abstract class AbstractCxfEndpointConfig {

	public static final int DELAY_BETWEEN_RETRIES_MS = 2_000;
	public static final int DEFAULT_MAX_NUMBER_OF_RETRIES = 2;

	private final JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();

	public AbstractCxfEndpointConfig() {
		factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		factoryBean.getInInterceptors().add(new LoggingInInterceptor());
		factoryBean.setProperties(new HashMap<>());
		retries(DEFAULT_MAX_NUMBER_OF_RETRIES);
	}

	void setAdress(String aktoerUrl) {
		factoryBean.setAddress(aktoerUrl);
	}

	void addInterceptor(Interceptor<? extends Message> interceptor) {
		factoryBean.getOutInterceptors().add(interceptor);
	}

	void retries(int maxNumberOfRetries) {
		FailoverFeature failoverFeature = new FailoverFeature();
		RetryStrategy retryStrategy = new RetryStrategy();
		retryStrategy.setDelayBetweenRetries(DELAY_BETWEEN_RETRIES_MS);
		failoverFeature.setStrategy(retryStrategy);

		retryStrategy.setMaxNumberOfRetries(maxNumberOfRetries);
		factoryBean.getFeatures().add(failoverFeature);
	}

	<T> T createPort(Class<T> portType) {
		return factoryBean.create(portType);
	}

	void enableMtom() {
		factoryBean.getProperties().put("mtom-enabled", true);
	}

}
