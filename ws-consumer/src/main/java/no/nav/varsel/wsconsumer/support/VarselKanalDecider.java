package no.nav.varsel.wsconsumer.support;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Decider for Varsel based on VarselInfo from dokkat and DigitalKontaktinformasjon from DKIF
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselKanalDecider {

	public Collection<KanalCode> decideKanaler(KontaktregisterTo kontaktregisterTo, Set<KanalCode> preferertKanalDki) {
		Set<KanalCode> kanaler = new HashSet<>();
		Set<KanalCode> preferertKanal = new HashSet<>();
		if (preferertKanalDki != null) {
			preferertKanal.addAll(preferertKanalDki);
		}
		if (preferertKanal.isEmpty()) {
			preferertKanal.add(SMS);
			preferertKanal.add(EPOST);
		}

		if (preferertKanal.contains(SMS) && preferertKanal.contains(EPOST)) {
			tryEpost(kanaler, kontaktregisterTo);
			trySms(kanaler, kontaktregisterTo);
		} else if (preferertKanal.contains(EPOST)) {
			tryEpost(kanaler, kontaktregisterTo);
			if (kanaler.isEmpty()) {
				trySms(kanaler, kontaktregisterTo);
			}
		} else if (preferertKanal.contains(SMS)) {
			trySms(kanaler, kontaktregisterTo);
			if (kanaler.isEmpty()) {
				tryEpost(kanaler, kontaktregisterTo);
			}
		}

		if (kanaler.isEmpty()) {
			kanaler.add(DITT_NAV);
		}
		return kanaler;
	}

	private void tryEpost(Set<KanalCode> kanaler, KontaktregisterTo kontaktregisterTo) {
		if (kontaktregisterTo.getEpostadresse() != null) {
			kanaler.add(EPOST);
		}
	}

	private void trySms(Set<KanalCode> kanaler, KontaktregisterTo kontaktregisterTo) {
		if (kontaktregisterTo.getMobiltelefonnummer() != null) {
			kanaler.add(SMS);
		}
	}
}
