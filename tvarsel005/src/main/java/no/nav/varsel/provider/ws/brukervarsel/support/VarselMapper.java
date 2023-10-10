package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.varsel.service.tvarsel005.to.VarselTo;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.springframework.util.Assert.notNull;

public class VarselMapper {

	public Varsel map(VarselTo varselTo) {
		notNull(varselTo, "The parameter varselTo can't be null.");

		Varsel result = new Varsel();
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
