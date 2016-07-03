package no.nav.varsel.provider.map.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.provider.map.HentVarselForBrukerRequestMapper;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerTo;
import org.springframework.util.Assert;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

/**
 * @author Lars Aune
 */
public class DefaultHentVarselForBrukerRequestMapper implements HentVarselForBrukerRequestMapper {
	@Override
	public HentVarselForBrukerTo map(HentVarselForBrukerRequest request) {
		Assert.notNull(request, "The parameter request can't be null");

		HentVarselForBrukerTo.Builder builder = new HentVarselForBrukerTo.Builder();
		return builder.aktoerId(request.getBruker() instanceof AktoerId ? ((AktoerId) request.getBruker()).getAktoerId() : null)
				.fnr(request.getBruker() instanceof Person ? ((Person) request.getBruker()).getIdent() : null)
				.datoFom(toLocalDateTime(request.getPeriode().getFom()))
				.datoTom(toLocalDateTime(request.getPeriode().getTom()))
				.build();

	}

	private LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
		LocalDateTime result = null;
		if (xmlGregorianCalendar != null) {
			result = xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
		}
		return result;
	}
}
