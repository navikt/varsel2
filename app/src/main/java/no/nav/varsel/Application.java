package no.nav.varsel;

import lombok.extern.slf4j.Slf4j;
import no.nav.varsel.config.AppConfig;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

import static java.lang.System.getenv;
import static java.lang.System.setProperty;

/**
 * Servlet 3.0 Spring Boot Application Initializer for Varsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import(AppConfig.class)
@EnableRetry
@Slf4j
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		setProperty("javax.net.ssl.keyStorePassword", getenv("VARSEL_CERT_KEYSTORE_PASSWORD"));
		SpringApplication.run(Application.class, args);
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

}

