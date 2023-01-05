package no.nav.varsel.repo.config;


import com.codahale.metrics.MetricRegistry;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfig extends MetricsConfigurerAdapter {

	private MetricRegistry metricRegistry = new MetricRegistry();

	@Override
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}

}
