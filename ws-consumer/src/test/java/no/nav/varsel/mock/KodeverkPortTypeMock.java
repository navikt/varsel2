package no.nav.varsel.mock;

import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkResponse;

import javax.jws.WebService;

/**
 * @author Lars Aune
 */
@WebService(
		name = "KodeverkPortType",
		targetNamespace = "http://nav.no/tjeneste/virksomhet/kodeverk/v2",
		serviceName = "Kodeverk_v2",
		portName = "Kodeverk_v2"
)
public class KodeverkPortTypeMock implements KodeverkPortType {
	@Override
	public FinnKodeverkListeResponse finnKodeverkListe(FinnKodeverkListeRequest finnKodeverkListeRequest) {
		return null;
	}

	@Override
	public HentKodeverkResponse hentKodeverk(HentKodeverkRequest hentKodeverkRequest) throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
		return null;
	}

	@Override
	public void ping() {
	}
}
