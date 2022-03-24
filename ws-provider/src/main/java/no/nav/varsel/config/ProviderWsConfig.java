package no.nav.varsel.config;

import no.nav.modig.presentation.logging.session.MDCFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for Web Service Provider.
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({CxfConfig.class, ProviderEndpointConfig.class, Tvarsel005Config.class, ServiceConfig.class, XacmlConfig.class})
public class ProviderWsConfig {

	@Bean
	public FilterRegistrationBean mdcFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new MDCVarselFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setEnabled(true);
		return filterRegistrationBean;
	}

}
