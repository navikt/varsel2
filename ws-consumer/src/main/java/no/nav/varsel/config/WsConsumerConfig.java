package no.nav.varsel.config;

import no.nav.varsel.wsconsumer.WsPingProvider;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.pdl.PdlIdentConsumer;
import no.nav.varsel.wsconsumer.sts.StsRestConsumer;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
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

	@Bean
	public WsPingProvider wsPingProvider() {
		return new WsPingProvider();
	}
}
