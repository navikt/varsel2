package no.nav.varsel.web.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;

/**
 * Listener for å eksponere Metrics
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@WebListener
public class MetricsServletContextListener extends MetricsServlet.ContextListener {

	@Inject
	private MetricRegistry metricRegistry;

	@Override
	protected MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
}
