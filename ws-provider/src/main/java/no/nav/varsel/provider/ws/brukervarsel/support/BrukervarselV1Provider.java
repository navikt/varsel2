package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.service.BrukervarselV1Service;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;

import javax.inject.Inject;

/**
 * Provider for Tvarsel005 HentVarselForBruker
 *
 * @author Lars Aune
 */
public class BrukervarselV1Provider implements BrukervarselV1 {

	@Inject
	private HentVarselForBrukerRequestMapper hentVarselForBrukerRequestMapper;

	@Inject
	private HentVarselForBrukerResponseMapper hentVarselForbrukerResponseMapper;

	@Inject
	private BrukervarselV1Service brukervarselV1Service;

	@Override
	public void ping() {
	}

	@Override
	public HentVarselForBrukerResponse hentVarselForBruker(HentVarselForBrukerRequest hentVarselForBrukerRequest)
			throws HentVarselForBrukerUgyldigInput {
		HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo =
				brukervarselV1Service.hentVarselForBruker(hentVarselForBrukerRequestMapper.map(hentVarselForBrukerRequest));
		return hentVarselForbrukerResponseMapper.map(hentVarselForBrukerResponseTo);
	}
}
