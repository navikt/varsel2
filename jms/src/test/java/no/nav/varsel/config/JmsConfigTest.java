package no.nav.varsel.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Itest for JMS context
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@SpringBootTest(classes = JmsTestConfig.class)
@ActiveProfiles({"itest", "local"})
public class JmsConfigTest {

	@Test
	public void shouldStartContext() {
		// itests using the config exists in jms consumer, ws provider, batch etc
	}
}