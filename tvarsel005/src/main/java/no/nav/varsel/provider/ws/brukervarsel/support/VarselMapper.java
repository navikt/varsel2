package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarsel;
import no.nav.varsel.service.tvarsel005.to.VarselTo;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.springframework.util.Assert.notNull;

public class VarselMapper {

	public WSVarsel map(VarselTo varselTo) {
		notNull(varselTo, "The parameter varselTo can't be null.");

		WSVarsel result = new WSVarsel();
		result.setKanal(varselTo.getKanal());
		result.setSendt(toXmlGregorianCalendar(varselTo.getSendtTidspunkt()));
		result.setDistribuert(toXmlGregorianCalendar(varselTo.getDistribusjonsTidspunkt()));
		result.setKontaktinfo(varselTo.getKontaktInfo());
		result.setVarseltittel(varselTo.getVarselTittel());
		result.setVarseltekst(varselTo.getVarselTekst());
		result.setVarselURL(varselTo.getVarselURL());
		result.setReVarsel(varselTo.isRevarsel());

		return result;
	}
}
