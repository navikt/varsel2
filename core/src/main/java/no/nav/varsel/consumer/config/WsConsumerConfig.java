package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import no.nav.varsel.ws.config.CxfConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
		CxfConfig.class,
		ConsumerEndpointConfig.class,
		RestConsumerConfig.class,
		PdlIdentConsumer.class,
		StsRestConsumer.class
})
@Configuration
public class WsConsumerConfig {

}
