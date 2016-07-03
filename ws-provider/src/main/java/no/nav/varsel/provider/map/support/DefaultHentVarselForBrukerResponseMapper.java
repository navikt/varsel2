package no.nav.varsel.provider.map.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Brukervarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.provider.map.HentVarselForBrukerResponseMapper;
import no.nav.varsel.provider.map.VarselbestillingMapper;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import org.springframework.util.Assert;

import javax.inject.Inject;

/**
 * @author Lars Aune
 */
public class DefaultHentVarselForBrukerResponseMapper implements HentVarselForBrukerResponseMapper {
	@Inject
	private VarselbestillingMapper varselbestillingMapper;

	@Override
	public HentVarselForBrukerResponse map(HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo) {
		Assert.notNull(hentVarselForBrukerResponseTo, "The parameter hentVarselForBrukerResponseTo can't be null.");

		HentVarselForBrukerResponse result = new HentVarselForBrukerResponse();
		result.setBrukervarsel(createBrukerVarsel(hentVarselForBrukerResponseTo));
		return result;
	}

	private Brukervarsel createBrukerVarsel(HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo) {
		Brukervarsel result = new Brukervarsel();
		hentVarselForBrukerResponseTo.getBrukersVarsler().forEach(varselbestillingTo -> result.getVarselbestillingListe().add(varselbestillingMapper.map(varselbestillingTo)));
		return result;
	}

	public void setVarselbestillingMapper(VarselbestillingMapper varselbestillingMapper) {
		this.varselbestillingMapper = varselbestillingMapper;
	}
}
