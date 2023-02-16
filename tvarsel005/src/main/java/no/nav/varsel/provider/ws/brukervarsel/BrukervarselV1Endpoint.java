package no.nav.varsel.provider.ws.brukervarsel;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.ws.brukervarsel.support.BrukervarselV1Provider;
import no.nav.varsel.provider.ws.brukervarsel.support.HentVarselForBrukerRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.transaction.Transactional;

@WebService(
		targetNamespace = "http://nav.no/tjeneste/virksomhet/brukervarsel/v1/",
		serviceName = "Brukervarsel_v1",
		portName = "Brukervarsel_v1Port"
)
@Transactional
public class BrukervarselV1Endpoint implements BrukervarselV1 {

	private static final Logger log = LoggerFactory.getLogger(BrukervarselV1Endpoint.class);

	private static final String BRUKERVARSEL_V1 = "varsel.tvarsel005.BrukervarselV1";
	private static final String BRUKERVARSEL_V1_PING = BRUKERVARSEL_V1 + ".ping";
	private static final String BRUKERVARSEL_V1_HENT_VARSEL_FOR_BRUKER = BRUKERVARSEL_V1 + ".hentVarselForBruker";
	static final String ACCESS_DENIED = "Access denied";

	@Autowired
	private BrukervarselV1Provider brukervarselV1Provider;

	@Autowired
	private HentVarselForBrukerRequestValidator hentVarselForBrukerRequestValidator;

	@Override
	public void ping() {
		brukervarselV1Provider.ping();
	}

	@Override
	public HentVarselForBrukerResponse hentVarselForBruker(HentVarselForBrukerRequest hentVarselForBrukerRequest) throws HentVarselForBrukerUgyldigInput {
		hentVarselForBrukerRequestValidator.validate(hentVarselForBrukerRequest);
		try {
			return brukervarselV1Provider.hentVarselForBruker(hentVarselForBrukerRequest);
		} catch (RuntimeException e) {
			// Hvorfor gjør vi dette?
			// Biblioteket vi er avhengig av modig-security-authorization kaster access denied feil som RuntimeException.
			// Selv om dette er funksjonelle feil. For mer info se no.nav.modig.security.tilgangskontroll.policy.pep.PEPImpl
			if (e.getMessage().startsWith(ACCESS_DENIED)) {
				log.warn(String.format("Access denied in operation %s IdentType=%s", BRUKERVARSEL_V1_HENT_VARSEL_FOR_BRUKER,
						SubjectHandler.getSubjectHandler().getIdentType()), e);
				throw new AuthorizationException(ACCESS_DENIED);
			} else {
				log.error("Teknisk feil: " + e.getMessage(), e);
				throw e;
			}
		}
	}
}
