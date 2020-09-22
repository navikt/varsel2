package no.nav.varsel.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Itest for ws consumer context
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WsConsumerTestConfig.class)
@ActiveProfiles({"itest"})
@DirtiesContext
public class WsConsumerConfigTest {

	@Test
	public void shouldStartContext() throws Exception {
		// itests using the config exists in jms consumer, ws provider, batch etc
	}

}