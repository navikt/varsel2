package no.nav.varsel.config;

import no.nav.varsel.web.BatchJobController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Root Web Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import(BatchJobController.class)
public class WebConfig {
}
