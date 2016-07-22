package no.nav.varsel.config;

import no.nav.varsel.config.support.TimeoutFeature;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;

import javax.inject.Inject;
import java.net.URL;
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

	protected void setAdress(String aktoerUrl) {
		factoryBean.setAddress(aktoerUrl);
	}

	protected void setWsdlUrl(String classPathResourceWsdlUrl) {
		factoryBean.setWsdlURL(getUrlFromClasspathResource(classPathResourceWsdlUrl));
	}

	protected void addOutInterceptor(Interceptor<? extends Message> interceptor) {
		factoryBean.getOutInterceptors().add(interceptor);
	}

	protected void addInnInterceptor(Interceptor<? extends Message> interceptor) {
		factoryBean.getInInterceptors().add(interceptor);
	}

	protected <T> T createPort(Class<T> portType) {
		factoryBean.getFeatures().add(new TimeoutFeature(timeout, timeout));
		return factoryBean.create(portType);
	}

	private static String getUrlFromClasspathResource(String classpathResource) {
		URL url = AbstractCxfEndpointConfig.class.getClassLoader().getResource(classpathResource);
		if (url != null) {
			return url.toString();
		}
		throw new IllegalStateException("Failed to find resource: " + classpathResource);
	}

	protected void enableMtom() {
		factoryBean.getProperties().put("mtom-enabled", true);
	}

	protected void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
