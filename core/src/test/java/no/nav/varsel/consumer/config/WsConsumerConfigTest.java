package no.nav.varsel.consumer.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureWireMock(port = 0)
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"itest", "local"})
public class WsConsumerConfigTest {

	@Test
	public void shouldStartContext() {
		// itests using the config exists in jms consumer, ws provider, batch etc
	}

}