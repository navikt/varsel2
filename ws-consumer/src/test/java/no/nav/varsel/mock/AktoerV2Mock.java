package no.nav.varsel.mock;

import static no.nav.varsel.repo.TestdataUtil.FUNKSJONELL_FEIL;
import static no.nav.varsel.repo.TestdataUtil.TEKNISK_FEIL;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentListeRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentListeResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdListeRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdListeResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdResponse;

import javax.jws.WebService;

/**
 * @author Andreas Skomedal, Visma Consulting.
 */
@WebService(
		name = "Aktoer_v2",
		targetNamespace = "http://nav.no/tjeneste/virksomhet/aktoer/v2",
		serviceName = "Aktoer",
		portName = "Aktoer_v2Port"
)
public class AktoerV2Mock implements AktoerV2 {

	public static final String PERSON_IDENT = "personIdentMocked";
	public static final String AKTOER_ID = "aktoerIdMocked";

	@Override
	public HentIdentForAktoerIdResponse hentIdentForAktoerId(HentIdentForAktoerIdRequest hentIdentForAktoerIdRequest) throws HentIdentForAktoerIdPersonIkkeFunnet {
		String aktoerId = hentIdentForAktoerIdRequest.getAktoerId();
		if (TEKNISK_FEIL.equals(aktoerId)) {
			throw new RuntimeException("feil i aktoer");
		} else if (FUNKSJONELL_FEIL.equals(aktoerId)) {
			throw new HentIdentForAktoerIdPersonIkkeFunnet("ikke funnet", new PersonIkkeFunnet());
		}
		HentIdentForAktoerIdResponse response = new HentIdentForAktoerIdResponse();
		response.setIdent(PERSON_IDENT);
		return response;
	}

	@Override
	public HentAktoerIdForIdentResponse hentAktoerIdForIdent(HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest) throws HentAktoerIdForIdentPersonIkkeFunnet {
		HentAktoerIdForIdentResponse response = new HentAktoerIdForIdentResponse();
		response.setAktoerId(AKTOER_ID);
		return response;
	}

	@Override
	public HentAktoerIdForIdentListeResponse hentAktoerIdForIdentListe(HentAktoerIdForIdentListeRequest hentAktoerIdForIdentListeRequest) {
		return null;
	}

	@Override
	public HentIdentForAktoerIdListeResponse hentIdentForAktoerIdListe(HentIdentForAktoerIdListeRequest hentIdentForAktoerIdListeRequest) {
		return null;
	}

	@Override
	public void ping() {

	}
}
