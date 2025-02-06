package no.nav.varsel.tvarsel001.jms.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = JmsTestConfig.class)
@ActiveProfiles({"itest"})
public class JmsConfigTest {

	@Test
	public void shouldStartContext() {
		// itests using the config exists in jms consumer, ws provider, batch etc
	}
}