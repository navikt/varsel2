package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSAktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarselbestilling;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.springframework.util.Assert.notNull;

public class VarselbestillingMapper {

	@Autowired
	private VarselMapper varselMapper;

	public WSVarselbestilling map(VarselbestillingTo varselbestillingTo) {
		notNull(varselbestillingTo, "The parameter varselbestillingTo can't be null.");

		WSVarselbestilling result = new WSVarselbestilling();
		result.setVarseltypeId(varselbestillingTo.getVarseltypeId());
		result.setPerson(getPerson(varselbestillingTo));
		result.setAktoerId(getAktoerId(varselbestillingTo));
		result.setBestilt(toXmlGregorianCalendar(varselbestillingTo.getBestillingstidspunkt()));
		result.setReVarselingsintervall(varselbestillingTo.getRevarslingsIntervall());
		result.setSisteVarselutsendelse(toXmlGregorianCalendar(varselbestillingTo.getSisteVarselUtsendelse()));
		varselbestillingTo.getVarsler().forEach(varselTo -> result.getVarselListe().add(varselMapper.map(varselTo)));
		return result;
	}

	private WSAktoerId getAktoerId(VarselbestillingTo varselbestillingTo) {
		WSAktoerId result = null;

		if (varselbestillingTo.getAktoerId() != null) {
			result = new WSAktoerId();
			result.setAktoerId(varselbestillingTo.getAktoerId());
		}

		return result;
	}

	private WSPerson getPerson(VarselbestillingTo varselbestillingTo) {
		WSPerson result = null;

		if (varselbestillingTo.getFnr() != null) {
			result = new WSPerson();
			result.setIdent(varselbestillingTo.getFnr());
		}

		return result;
	}

	public void setVarselMapper(VarselMapper varselMapper) {
		this.varselMapper = varselMapper;
	}
}
