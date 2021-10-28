package no.nav.varsel.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@AutoConfigureWireMock(port = 0)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"itest", "local"})
public class WsConsumerConfigTest {

	@Test
	public void shouldStartContext() {
		// itests using the config exists in jms consumer, ws provider, batch etc
	}

}