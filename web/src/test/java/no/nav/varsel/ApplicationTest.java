package no.nav.varsel;

import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.config.WebTestConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Application config test
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebTestConfig.class)
@ActiveProfiles({"itest"})
@DirtiesContext
public class ApplicationTest {

	@BeforeClass
	public static void startup() throws Exception {
		JmsTestConfig.mockJndi();
	}

	@Test
	public void shouldStartContext() throws Exception {


	}
}