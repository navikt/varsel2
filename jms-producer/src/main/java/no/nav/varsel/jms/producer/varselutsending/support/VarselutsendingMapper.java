package no.nav.varsel.jms.producer.varselutsending.support;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;

import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Aktoer;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.AktoerId;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Kommunikasjonskanaler;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.PersonIdent;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Varselutsending;
import no.nav.melding.virksomhet.varselutsending.v1.varselutsending.Varslingstyper;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;

/**
 * Mapper for Varselutsending
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingMapper {

	public Varselutsending map(VarselutsendingTo to) {
		Varselutsending varselutsending = new Varselutsending();
		Kommunikasjonskanaler kommunikasjonskanaler = new Kommunikasjonskanaler();
		kommunikasjonskanaler.setValue(to.getKanal().toString());
		varselutsending.setKanal(kommunikasjonskanaler);

		varselutsending.setUtloepstidspunkt(toXmlGregorianCalendar(to.getUtloepstidspunkt()));
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

}
