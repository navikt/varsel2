package no.nav.varsel.consumer.mock;

import static no.nav.varsel.repo.TestdataUtil.PERSONIDENT_WHITESPACE_TEST;
import static no.nav.varsel.consumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.createResponse;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonBolkForMangeForespoersler;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonBolkSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentSikkerDigitalPostadresseBolkForMangeForespoersler;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentSikkerDigitalPostadresseBolkSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentSikkerDigitalPostadresseKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentSikkerDigitalPostadressePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentSikkerDigitalPostadresseSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonBolkRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonBolkResponse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentPrintsertifikatRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentPrintsertifikatResponse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentSikkerDigitalPostadresseBolkRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentSikkerDigitalPostadresseBolkResponse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentSikkerDigitalPostadresseRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentSikkerDigitalPostadresseResponse;

import javax.jws.WebService;

/**
 * @author Andreas Skomedal, Visma Consulting.
 */
@WebService(
		name = "DigitalKontaktinformasjon_v1",
		targetNamespace = "http://nav.no/tjeneste/virksomhet/digitalKontaktinformasjon/v1",
		serviceName = "DigitalKontaktinformasjon_v1",
		portName = "DigitalKontaktinformasjon_v1Port"
)
public class DigitalKontaktinformasjonV1Mock implements DigitalKontaktinformasjonV1 {
	@Override
	public HentSikkerDigitalPostadresseResponse hentSikkerDigitalPostadresse(HentSikkerDigitalPostadresseRequest hentSikkerDigitalPostadresseRequest) throws HentSikkerDigitalPostadresseKontaktinformasjonIkkeFunnet, HentSikkerDigitalPostadressePersonIkkeFunnet, HentSikkerDigitalPostadresseSikkerhetsbegrensing {
		return null;
	}

	@Override
	public HentPrintsertifikatResponse hentPrintsertifikat(HentPrintsertifikatRequest hentPrintsertifikatRequest) {
		return null;
	}

	@Override
	public HentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(HentDigitalKontaktinformasjonRequest hentDigitalKontaktinformasjonRequest) throws HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonPersonIkkeFunnet, HentDigitalKontaktinformasjonSikkerhetsbegrensing {
		HentDigitalKontaktinformasjonResponse response = createResponse();
		if(PERSONIDENT_WHITESPACE_TEST.equals(hentDigitalKontaktinformasjonRequest.getPersonident())) {
			response.getDigitalKontaktinformasjon().getEpostadresse().setValue(
					response.getDigitalKontaktinformasjon().getEpostadresse().getValue() + " ");
		}
		return response;
	}

	@Override
	public void ping() {

	}

	@Override
	public HentDigitalKontaktinformasjonBolkResponse hentDigitalKontaktinformasjonBolk(HentDigitalKontaktinformasjonBolkRequest hentDigitalKontaktinformasjonBolkRequest) throws HentDigitalKontaktinformasjonBolkForMangeForespoersler, HentDigitalKontaktinformasjonBolkSikkerhetsbegrensing {
		return null;
	}

	@Override
	public HentSikkerDigitalPostadresseBolkResponse hentSikkerDigitalPostadresseBolk(HentSikkerDigitalPostadresseBolkRequest hentSikkerDigitalPostadresseBolkRequest) throws HentSikkerDigitalPostadresseBolkForMangeForespoersler, HentSikkerDigitalPostadresseBolkSikkerhetsbegrensing {
		return null;
	}
}
