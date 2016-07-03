package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.map.HentVarselForBrukerRequestMapper;
import no.nav.varsel.provider.map.HentVarselForBrukerResponseMapper;
import no.nav.varsel.provider.ws.handler.Validator;
import no.nav.varsel.service.BrukervarselV1Service;
import no.nav.varsel.service.PingService;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;

import javax.inject.Inject;

/**
 * @author Lars Aune
 */
public class BrukervarselV1Provider implements BrukervarselV1 {

	@Inject
	private PingService pingService;

	@Inject
	private Validator<HentVarselForBrukerRequest> hentVarselForBrukerRequestValidator;

	@Inject
	private HentVarselForBrukerRequestMapper hentVarselForBrukerRequestMapper;

	@Inject
	private BrukervarselV1Service brukervarselV1Service;

	@Inject
	private HentVarselForBrukerResponseMapper hentVarselForbrukerResponseMapper;

	@Override
	public void ping() {
		pingService.ping();
	}

	@Override
	public HentVarselForBrukerResponse hentVarselForBruker(HentVarselForBrukerRequest hentVarselForBrukerRequest)
			throws HentVarselForBrukerUgyldigInput {

		hentVarselForBrukerRequestValidator.validate(hentVarselForBrukerRequest);
		HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo =
				brukervarselV1Service.hentVarselForBruker(hentVarselForBrukerRequestMapper.map(hentVarselForBrukerRequest));
		return hentVarselForbrukerResponseMapper.map(hentVarselForBrukerResponseTo);
	}
}
