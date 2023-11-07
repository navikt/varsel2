package no.nav.varsel.provider.ws.brukervarsel.support;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.ws.brukervarsel.AuthorizationException;
import no.nav.varsel.service.interfaces.BrukervarselService;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.core.domain.IdentType.InternBruker;

@Slf4j
public class BrukervarselV1Provider {

	@Autowired
	private HentVarselForBrukerRequestMapper hentVarselForBrukerRequestMapper;

	@Autowired
	private HentVarselForBrukerResponseMapper hentVarselForbrukerResponseMapper;

	@Autowired
	private BrukervarselService brukervarselV1Service;

	public void ping() {
		//ping
	}

	public HentVarselForBrukerResponse hentVarselForBruker(HentVarselForBrukerRequest hentVarselForBrukerRequest) {

		if (getSubjectHandler().getIdentType() != InternBruker) {
			throw new AuthorizationException("Access denied");
		}

		HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo =
				brukervarselV1Service.hentVarselForBruker(hentVarselForBrukerRequestMapper.map(hentVarselForBrukerRequest));

		return hentVarselForbrukerResponseMapper.map(hentVarselForBrukerResponseTo);
	}
}
