package no.nav.varsel;

import no.nav.varsel.config.WebTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WebTestConfig.class)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles({"itest", "local"})
public class ApplicationTest {

	@Test
	public void shouldStartContext() {}
}