package no.nav.varsel.config;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import no.nav.varsel.web.metrics.MetricsServletContextListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for Metrics
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfig extends MetricsConfigurerAdapter {

	private MetricRegistry registry = new MetricRegistry();

	@Bean
	public MetricsServlet.ContextListener metricContextListener() {
		return new MetricsServletContextListener();
	}

	@Override
	public MetricRegistry getMetricRegistry() {
		return registry;
	}

}
