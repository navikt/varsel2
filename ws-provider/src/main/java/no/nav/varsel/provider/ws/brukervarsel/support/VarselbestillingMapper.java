package no.nav.varsel.provider.ws.brukervarsel.support;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.springframework.util.Assert;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Response mapper for Tvarsel005 HentVarselForBruker
 *
 * @author Lars Aune
 */
public class VarselbestillingMapper {

	@Autowired
	private VarselMapper varselMapper;

	public Varselbestilling map(VarselbestillingTo varselbestillingTo) {
		Assert.notNull(varselbestillingTo, "The parameter varselbestillingTo can't be null.");

		Varselbestilling result = new Varselbestilling();
		result.setVarseltypeId(varselbestillingTo.getVarseltypeId());
		result.setPerson(getPerson(varselbestillingTo));
		result.setAktoerId(getAktoerId(varselbestillingTo));
		result.setBestilt(toXmlGregorianCalendar(varselbestillingTo.getBestillingstidspunkt()));
		result.setReVarselingsintervall(varselbestillingTo.getRevarslingsIntervall());
		result.setSisteVarselutsendelse(toXmlGregorianCalendar(varselbestillingTo.getSisteVarselUtsendelse()));
		varselbestillingTo.getVarsler().forEach(varselTo -> result.getVarselListe().add(varselMapper.map(varselTo)));
		return result;
	}

	private AktoerId getAktoerId(VarselbestillingTo varselbestillingTo) {
		AktoerId result = null;
		if (varselbestillingTo.getAktoerId() != null) {
			result = new AktoerId();
			result.setAktoerId(varselbestillingTo.getAktoerId());
		}
		return result;
	}

	private Person getPerson(VarselbestillingTo varselbestillingTo) {
		Person result = null;
		if (varselbestillingTo.getFnr() != null) {
			result = new Person();
			result.setIdent(varselbestillingTo.getFnr());
		}
		return result;
	}

	public void setVarselMapper(VarselMapper varselMapper) {
		this.varselMapper = varselMapper;
	}
}
