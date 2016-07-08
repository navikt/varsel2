package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import org.springframework.util.Assert;

/**
 * Response mapper for Tvarsel005 HentVarselForBruker
 *
 * @author Lars Aune
 */
public class VarselMapper {

	public Varsel map(VarselTo varselTo) {
		Assert.notNull(varselTo, "The parameter varselTo can't be null.");
		Varsel result = new Varsel();
		result.setKanal(varselTo.getKanal());
		result.setSendt(XmlGregorianConverter.toXmlGregorianCalendar(varselTo.getSendtTidspunkt()));
		result.setDistribuert(XmlGregorianConverter.toXmlGregorianCalendar(varselTo.getDistribusjonsTidspunkt()));
		result.setKontaktinfo(varselTo.getKontaktInfo());
		result.setVarseltittel(varselTo.getVarselTittel());
		result.setVarseltekst(varselTo.getVarselTekst());
		result.setVarselURL(varselTo.getVarselURL());
		result.setReVarsel(varselTo.isRevarsel());
		return result;
	}
}
