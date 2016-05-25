package no.nav.varsel.config;

import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring config for WS Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
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
	public VarselInfoConsumer varselInfoConsumer() {
		return new VarselInfoConsumer();
	}
}
