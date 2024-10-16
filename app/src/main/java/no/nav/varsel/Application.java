package no.nav.varsel;

import no.nav.varsel.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Servlet 3.0 Spring Boot Application Initializer for Varsel
 */
@Import(AppConfig.class)
@EnableRetry
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

