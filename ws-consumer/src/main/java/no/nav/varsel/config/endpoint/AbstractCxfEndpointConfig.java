package no.nav.varsel.config.endpoint;

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

	@Inject
	private SpringBus bus;

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

	void addInterceptor(Interceptor<? extends Message> interceptor) {
		factoryBean.getOutInterceptors().add(interceptor);
	}

	<T> T createPort(Class<T> portType) {
		return factoryBean.create(portType);
	}

	void enableMtom() {
		factoryBean.getProperties().put("mtom-enabled", true);
	}

}
