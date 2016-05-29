package no.nav.varsel.service;

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
public class VarslelKanalDecider {

	public static final String SMS_EPOST = "SMS_EPOST";

	public Collection<KanalCode> decideKanaler(KontaktregisterTo kontaktregisterTo, String preferertKanal) {
		Set<KanalCode> kanaler = new HashSet<>();
		preferertKanal = preferertKanal == null ? SMS_EPOST : preferertKanal;

		if (preferertKanal.equals(SMS_EPOST)) {
			prefSmsEpost(kanaler, kontaktregisterTo);
		} else if (preferertKanal.equals(KanalCode.EPOST.toString())) {
			tryEpost(kanaler, kontaktregisterTo);
			if (kanaler.size() == 0) {
				trySms(kanaler, kontaktregisterTo);
			}
		} else if (preferertKanal.equals(KanalCode.SMS.toString())) {
			trySms(kanaler, kontaktregisterTo);
			if (kanaler.size() == 0) {
				tryEpost(kanaler, kontaktregisterTo);
			}
		}

		if (kanaler.size() == 0) {
			kanaler.add(KanalCode.DITTNAV);
		}
		return kanaler;
	}

	private void prefSmsEpost(Set<KanalCode> kanaler, KontaktregisterTo kontaktregisterTo) {
		tryEpost(kanaler, kontaktregisterTo);
		trySms(kanaler, kontaktregisterTo);
	}

	private void tryEpost(Set<KanalCode> kanaler, KontaktregisterTo kontaktregisterTo) {
		if (kontaktregisterTo.getEpostadresse() != null) {
			kanaler.add(KanalCode.EPOST);
		}
	}

	private void trySms(Set<KanalCode> kanaler, KontaktregisterTo kontaktregisterTo) {
		if (kontaktregisterTo.getMobiltelefonnummer() != null) {
			kanaler.add(SMS);
		}
	}
}
