package no.nav.varsel.wsconsumer;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.varsel.domain.to.Ping;
import no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.varsel.domain.to.Ping.Type.Rest;
import static no.nav.varsel.domain.to.Ping.Type.Soap;

/**
 * Ping Provider for Ws Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class WsPingProvider {

	@Autowired
	private DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1;
	@Value("${dkif.ws.endpointUrl}")
	private String dkifUrl;
	@Autowired
	private VarselInfoConsumer varselInfoConsumer;
	@Value("${dokkat.varselinfo.rest.url}")
	private String varselInfoUrl;

	public Ping pingDigitalKontaktinformasjonV1() {
		return new Ping(Soap, "DigitalKontaktinformasjonV1", dkifUrl, () -> digitalKontaktinformasjonV1.ping());
	}

	public Ping pingVarselInfoV1() {
		return new Ping(Rest, "VarselInfoV1", varselInfoUrl, () -> varselInfoConsumer.ping());
	}
}
