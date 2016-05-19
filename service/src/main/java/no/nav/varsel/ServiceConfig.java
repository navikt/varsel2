package no.nav.varsel;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@Import({RepoConfig.class})
public class ServiceConfig {
}
