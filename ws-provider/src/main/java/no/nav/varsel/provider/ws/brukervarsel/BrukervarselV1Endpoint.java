package no.nav.varsel.provider.ws.brukervarsel;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.ws.brukervarsel.support.HentVarselForBrukerRequestValidator;
import no.nav.varsel.provider.ws.brukervarsel.support.BrukervarselV1Provider;

import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebService;

/**
 * Endpoint for BrukervarselV1 TVARSEL005
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@WebService(
		targetNamespace = "http://nav.no/tjeneste/virksomhet/brukervarsel/v1/",
		serviceName = "Brukervarsel_v1",
		portName = "Brukervarsel_v1Port"
)
@HandlerChain(file = "/modig-jboss-provider-handlers.xml")
public class BrukervarselV1Endpoint implements BrukervarselV1 {

	private static final String BRUKERVARSEL_V1 = "BrukervarselV1";
	private static final String BRUKERVARSEL_V1_PING = BRUKERVARSEL_V1 + ".ping";
	private static final String BRUKERVARSEL_V1_HENT_VARSEL_FOR_BRUKER = BRUKERVARSEL_V1 + ".hentVarselForBruker";

	@Inject
	private BrukervarselV1Provider brukervarselV1Provider;

	@Inject
	private HentVarselForBrukerRequestValidator hentVarselForBrukerRequestValidator;

	@Override
	@Counted(name = BRUKERVARSEL_V1_PING + ".count", absolute = true, monotonic = true)
	@Timed(name = BRUKERVARSEL_V1_PING, absolute = true)
	@ExceptionMetered(name = BRUKERVARSEL_V1_PING + ".exceptions", absolute = true)
	public void ping() {
		brukervarselV1Provider.ping();
	}

	@Override
	@Counted(name = BRUKERVARSEL_V1_HENT_VARSEL_FOR_BRUKER + ".count", absolute = true, monotonic = true)
	@Timed(name = BRUKERVARSEL_V1_HENT_VARSEL_FOR_BRUKER, absolute = true)
	@ExceptionMetered(name = BRUKERVARSEL_V1_HENT_VARSEL_FOR_BRUKER + ".exceptions", absolute = true)
	public HentVarselForBrukerResponse hentVarselForBruker(HentVarselForBrukerRequest hentVarselForBrukerRequest) throws HentVarselForBrukerUgyldigInput {
		hentVarselForBrukerRequestValidator.validate(hentVarselForBrukerRequest);
		return brukervarselV1Provider.hentVarselForBruker(hentVarselForBrukerRequest);
	}
}
