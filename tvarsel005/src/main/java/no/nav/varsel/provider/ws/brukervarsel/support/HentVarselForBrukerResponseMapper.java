package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Brukervarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class HentVarselForBrukerResponseMapper {
	@Autowired
	private VarselbestillingMapper varselbestillingMapper;

	public HentVarselForBrukerResponse map(HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo) {
		Assert.notNull(hentVarselForBrukerResponseTo, "The parameter hentVarselForBrukerResponseTo can't be null.");

		HentVarselForBrukerResponse result = new HentVarselForBrukerResponse();
		result.setBrukervarsel(createBrukerVarsel(hentVarselForBrukerResponseTo));
		return result;
	}

	private Brukervarsel createBrukerVarsel(HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo) {
		Brukervarsel result = new Brukervarsel();
		hentVarselForBrukerResponseTo.getVarselbestillingTos().forEach(varselbestillingTo -> result.getVarselbestillingListe().add(varselbestillingMapper.map(varselbestillingTo)));
		return result;
	}

	public void setVarselbestillingMapper(VarselbestillingMapper varselbestillingMapper) {
		this.varselbestillingMapper = varselbestillingMapper;
	}
}
