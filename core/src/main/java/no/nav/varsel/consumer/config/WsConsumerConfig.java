package no.nav.varsel.consumer.config;

import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.consumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.consumer.sts.StsRestConsumer;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.consumer.pdl.PdlIdentConsumer;
import no.nav.varsel.ws.config.CxfConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
		STSConfig.class,
		CxfConfig.class,
		ConsumerEndpointConfig.class,
		RestConsumerConfig.class,
		PdlIdentConsumer.class,
		StsRestConsumer.class
})
@Configuration
public class WsConsumerConfig {

	@Bean
	public HentDigitalKontaktinformasjonConsumer hentDigitalKontaktinformasjonConsumer() {
		return new HentDigitalKontaktinformasjonConsumer();
	}

	@Bean
	public HentDigitalKontaktinformasjonMapper hentDigitalKontaktinformasjonMapper() {
		return new HentDigitalKontaktinformasjonMapper();
	}

	@Bean
	public VarselInfoConsumer varselInfoConsumer() {
		return new VarselInfoConsumer();
	}

	@Bean
	public VarselKanalDecider varselKanalDecider() {
		return new VarselKanalDecider();
	}

}
