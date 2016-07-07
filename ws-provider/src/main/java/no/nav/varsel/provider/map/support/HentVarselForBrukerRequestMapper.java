package no.nav.varsel.provider.map.support;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;
import org.springframework.util.Assert;

/**
 * @author Lars Aune
 */
public class HentVarselForBrukerRequestMapper {
	public HentVarselForBrukerTo map(HentVarselForBrukerRequest request) {
		Assert.notNull(request, "The parameter request can't be null");

		HentVarselForBrukerTo.Builder builder = new HentVarselForBrukerTo.Builder();
		Periode periode = request.getPeriode() == null ? new Periode() : request.getPeriode();
		return builder.aktoerId(request.getBruker() instanceof AktoerId ? ((AktoerId) request.getBruker()).getAktoerId() : null)
				.fnr(request.getBruker() instanceof Person ? ((Person) request.getBruker()).getIdent() : null)
				.datoFom(toLocalDateTime(periode.getFom()))
				.datoTom(toLocalDateTime(periode.getTom()))
				.build();

	}
}
