package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;
import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo.Builder.aHentVarselForBrukerTo;
import static org.springframework.util.Assert.notNull;

public class HentVarselForBrukerRequestMapper {

	public HentVarselForBrukerTo map(HentVarselForBrukerRequest request) {
		notNull(request, "The parameter request can't be null");

		HentVarselForBrukerTo.Builder builder = aHentVarselForBrukerTo();
		Periode periode = request.getPeriode() == null ? new Periode() : request.getPeriode();

		return builder.aktoerId(request.getBruker() instanceof AktoerId ? ((AktoerId) request.getBruker()).getAktoerId() : null)
				.fnr(request.getBruker() instanceof Person ? ((Person) request.getBruker()).getIdent() : null)
				.datoFom(toLocalDateTime(periode.getFom()))
				.datoTom(toLocalDateTime(periode.getTom()))
				.build();
	}
}
