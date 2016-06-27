package no.nav.varsel.config;

import no.nav.varsel.wsconsumer.WsPingProvider;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.wsconsumer.kodeverk.KodeverkConsumer;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring config for WS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Import({CxfConfig.class, ConsumerEndpointConfig.class, RestConsumerConfig.class})
@Configuration
public class WsConsumerConfig {

	@Bean
	public AktoerConsumer aktoerConsumer() {
		return new AktoerConsumer();
	}

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
	public KodeverkConsumer kodeverkConsumer() {
		return new KodeverkConsumer();
	}

	@Bean
	public WsPingProvider wsPingProvider() {
		return new WsPingProvider();
	}
}
