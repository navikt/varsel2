package no.nav.varsel.wsconsumer;

import static no.nav.varsel.domain.to.Ping.Type.Soap;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.varsel.domain.to.Ping;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

/**
 * Ping Provider for Ws Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class WsPingProvider {

	@Inject
	private AktoerV2 aktoerV2;
	@Value("${aktoerv2.ws.url}")
	private String aktoerUrl;

	@Inject
	private KodeverkPortType kodeverkPortType;
	@Value("${kodeverkv2.ws.url}")
	private String kodeverkUrl;

	@Inject
	private DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1;
	@Value("${dkif.ws.url}")
	private String dkifUrl;

	public Ping pingAktoerV2() {
		return new Ping(Soap, "AktoerV2", aktoerUrl, () -> aktoerV2.ping());
	}

	public Ping pingDigitalKontaktinformasjonV1() {
		return new Ping(Soap, "DigitalKontaktinformasjonV1", dkifUrl, () -> digitalKontaktinformasjonV1.ping());
	}

	public Ping pingKodeverkPortType() {
		return new Ping(Soap, "KodeverkPortType", kodeverkUrl, () -> kodeverkPortType.ping());
	}
}
