package no.nav.varsel.consumer.config;


import no.nav.varsel.consumer.config.cache.LokalCacheConfig;
import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
		WsConsumerConfig.class,
		PdlIdentConsumer.class,
		StsRestConsumer.class,
		LokalCacheConfig.class
})
@Configuration
public class WsConsumerTestConfig {

}