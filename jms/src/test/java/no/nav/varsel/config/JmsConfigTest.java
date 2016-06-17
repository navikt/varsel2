package no.nav.varsel.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Itest for JMS context
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JmsTestConfig.class)
@ActiveProfiles({"itest"})
@DirtiesContext
public class JmsConfigTest {

	@BeforeClass
	public static void setUpStatic() throws Exception {
		JmsTestConfig.mockJndi();
	}

	@Test
	public void shouldStartContext() throws Exception {
		// itests using the config exists in jms consumer, ws provider, batch etc
	}
}