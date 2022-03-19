package no.nav.varsel;

import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.config.WebTestConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebTestConfig.class)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles({"itest", "local"})
public class ApplicationTest {

	@Test
	public void shouldStartContext() {}
}