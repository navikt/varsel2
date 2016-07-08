package no.nav.varsel.config;

import no.nav.brevogarkiv.batch.common.provider.launch.support.ModigJobOperator;
import no.nav.brevogarkiv.batch.common.provider.rs.ModigJobController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Root Web Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
public class WebConfig {

	@Bean
	public ModigJobController modigJobController(ModigJobOperator modigJobOperator) {
		ModigJobController modigJobController = new ModigJobController();
		modigJobController.setModigJobOperator(modigJobOperator);
		return modigJobController;
	}
}
