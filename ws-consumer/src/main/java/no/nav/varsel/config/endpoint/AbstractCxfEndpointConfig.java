package no.nav.varsel.config.endpoint;

import no.nav.varsel.config.endpoint.support.TimeoutFeature;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * Abstract helper class for Cxf Endpoints, retries are handled by JMS retries
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public abstract class AbstractCxfEndpointConfig {

	public static final int DEFAULT_TIMEOUT = 30_000;

	@Inject
	private SpringBus bus;

	private int timeout = DEFAULT_TIMEOUT;
	private final JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();

	public AbstractCxfEndpointConfig() {
		factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
		factoryBean.getInInterceptors().add(new LoggingInInterceptor());
		factoryBean.setProperties(new HashMap<>());
		factoryBean.setBus(bus);
	}

	void setAdress(String aktoerUrl) {
		factoryBean.setAddress(aktoerUrl);
	}

	void addOutInterceptor(Interceptor<? extends Message> interceptor) {
		factoryBean.getOutInterceptors().add(interceptor);
	}

	void addInnInterceptor(Interceptor<? extends Message> interceptor) {
		factoryBean.getInInterceptors().add(interceptor);
	}

	<T> T createPort(Class<T> portType) {
		factoryBean.getFeatures().add(new TimeoutFeature(timeout, timeout));
		return factoryBean.create(portType);
	}

	void enableMtom() {
		factoryBean.getProperties().put("mtom-enabled", true);
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
