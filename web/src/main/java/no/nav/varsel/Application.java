package no.nav.varsel;

import com.codahale.metrics.servlets.MetricsServlet;
import no.nav.varsel.config.AppConfig;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Servlet 3.0 Spring Boot Application Initializer for Varsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceTransactionManagerAutoConfiguration.class, DataSourceAutoConfiguration.class})
@Import(AppConfig.class)
public class Application extends SpringBootServletInitializer {

	@Bean
	public WebMvcConfigurerAdapter dispatcherServletConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				// static content for selftest etc
				registry.addResourceHandler("/internal/*")
						.addResourceLocations("classpath:/web/static/css/");
			}
		};
	}

	@Bean(name = "webServiceServlet")
	public ServletRegistrationBean webServiceServlet() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
		servletRegistrationBean.setServlet(new CXFServlet());
		servletRegistrationBean.setName("webServiceServlet");
		servletRegistrationBean.addUrlMappings("/ws/*");
		servletRegistrationBean.setLoadOnStartup(2);
		return servletRegistrationBean;
	}

	@Bean
	public ServletRegistrationBean metricsServlet() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
		servletRegistrationBean.setServlet(new MetricsServlet());
		servletRegistrationBean.setName("metricsServlet");
		servletRegistrationBean.addUrlMappings("/internal/metrics/*");
		servletRegistrationBean.setLoadOnStartup(3);
		return servletRegistrationBean;
	}

}

