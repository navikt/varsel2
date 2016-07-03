package no.nav.varsel.provider.map.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import no.nav.varsel.provider.map.VarselMapper;
import no.nav.varsel.provider.map.VarselbestillingMapper;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

/**
 * @author Lars Aune
 */
public class DefaultVarselbestillingMapper implements VarselbestillingMapper {

	@Inject
	private VarselMapper varselMapper;

	@Override
	public Varselbestilling map(VarselbestillingTo varselbestillingTo) {
		Assert.notNull(varselbestillingTo, "The parameter varselbestillingTo can't be null.");

		Varselbestilling result = new Varselbestilling();
		result.setVarseltypeId(varselbestillingTo.getVarseltypeId());
		result.setPerson(getPerson(varselbestillingTo));
		result.setAktoerId(getAktoerId(varselbestillingTo));
		result.setBestilt(getBestilt(varselbestillingTo));
		result.setReVarselingsintervall(varselbestillingTo.getRevarslingsIntervall());
		result.setSisteVarselutsendelse(getSisteVarselutsendelse(varselbestillingTo.getSisteVarselUtsendelse()));
		varselbestillingTo.getVarsler().forEach(varselTo -> result.getVarselListe().add(varselMapper.map(varselTo)));
		return result;
	}

	private XMLGregorianCalendar getSisteVarselutsendelse(LocalDateTime sisteVarselUtsendelse) {
		return XmlGregorianConverter.toXmlGregorianCalendar(sisteVarselUtsendelse);
	}

	private XMLGregorianCalendar getBestilt(VarselbestillingTo varselbestillingTo) {
		return XmlGregorianConverter.toXmlGregorianCalendar(varselbestillingTo.getBestillingstidspunkt());
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
