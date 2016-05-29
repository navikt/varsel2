package no.nav.varsel.jms.producer.varselutsending.support;

import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Aktoer;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.AktoerId;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Kommunikasjonskanaler;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.PersonIdent;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Varselutsending;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Varslingstyper;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

/**
 * Mapper for Varselutsending
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingMapper {

	private DatatypeFactory datatypeFactory;

	public VarselutsendingMapper() throws DatatypeConfigurationException {
		this.datatypeFactory = DatatypeFactory.newInstance();
	}

	public Varselutsending map(VarselutsendingTo to) {
		Varselutsending varselutsending = new Varselutsending();
		Kommunikasjonskanaler kommunikasjonskanaler = new Kommunikasjonskanaler();
		kommunikasjonskanaler.setValue(to.getKanal().toString());
		varselutsending.setKanal(kommunikasjonskanaler);

		varselutsending.setUtloepstidspunkt(convert(to.getUtloepstidspunkt()));
		Varslingstyper varslingstype = new Varslingstyper();
		varslingstype.setValue(to.getVarslingstype());
		varselutsending.setVarslingstype(varslingstype);
		varselutsending.setMottaker(convert(to.getMottaker()));
		varselutsending.setVarselId(to.getVarselId());
		varselutsending.setVarselURL(to.getVarselUrl());
		varselutsending.setVarselTekst(to.getVarselTekst());
		varselutsending.setVarselTittel(to.getVarselTittel());
		return varselutsending;
	}

	private Aktoer convert(AktoerTo mottaker) {
		if (mottaker.getMottakerType().equals(MottakerType.AKTOER)) {
			AktoerId aktoerId = new AktoerId();
			aktoerId.setAktoerId(mottaker.getIdent());
			return aktoerId;
		}
		PersonIdent personIdent = new PersonIdent();
		personIdent.setPersonIdent(mottaker.getIdent());
		return personIdent;
	}

	private XMLGregorianCalendar convert(LocalDateTime localDateTime) {
		GregorianCalendar calendar = GregorianCalendar.from(localDateTime.atZone(ZoneId.systemDefault()));
		return datatypeFactory.newXMLGregorianCalendar(calendar);
	}
}
