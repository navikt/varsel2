package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSBrukervarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerResponse;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.util.Assert.notNull;

public class HentVarselForBrukerResponseMapper {

	@Autowired
	private VarselbestillingMapper varselbestillingMapper;

	public WSHentVarselForBrukerResponse map(HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo) {
		notNull(hentVarselForBrukerResponseTo, "The parameter hentVarselForBrukerResponseTo can't be null.");

		WSHentVarselForBrukerResponse result = new WSHentVarselForBrukerResponse();
		result.setBrukervarsel(createBrukerVarsel(hentVarselForBrukerResponseTo));

		return result;
	}

	private WSBrukervarsel createBrukerVarsel(HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo) {
		WSBrukervarsel result = new WSBrukervarsel();
		hentVarselForBrukerResponseTo.getVarselbestillingTos().forEach(varselbestillingTo -> result.getVarselbestillingListe().add(varselbestillingMapper.map(varselbestillingTo)));

		return result;
	}

	public void setVarselbestillingMapper(VarselbestillingMapper varselbestillingMapper) {
		this.varselbestillingMapper = varselbestillingMapper;
	}
}
