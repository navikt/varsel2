package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSAktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;
import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo.Builder.aHentVarselForBrukerTo;
import static org.springframework.util.Assert.notNull;

public class HentVarselForBrukerRequestMapper {

	public HentVarselForBrukerTo map(WSHentVarselForBrukerRequest request) {
		notNull(request, "The parameter request can't be null");

		HentVarselForBrukerTo.Builder builder = aHentVarselForBrukerTo();
		WSPeriode periode = request.getPeriode() == null ? new WSPeriode() : request.getPeriode();

		return builder.aktoerId(request.getBruker() instanceof WSAktoerId ? ((WSAktoerId) request.getBruker()).getAktoerId() : null)
				.fnr(request.getBruker() instanceof WSPerson ? ((WSPerson) request.getBruker()).getIdent() : null)
				.datoFom(toLocalDateTime(periode.getFom()))
				.datoTom(toLocalDateTime(periode.getTom()))
				.build();
	}
}
