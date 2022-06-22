package no.nav.varsel.consumer.dkif.support;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;
import static no.nav.varsel.consumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.Kontaktinformasjon;
import no.nav.varsel.consumer.dkif.DigitalKontaktInfoResponse;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import org.apache.commons.lang3.StringUtils;


/**
 * Map response from HentDigitalKontaktinformasjon
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HentDigitalKontaktinformasjonMapper {
	
	public KontaktregisterTo map(DigitalKontaktInfoResponse.DigitalKontaktinfo dki) {

		
		KontaktregisterTo.KontaktregisterToBuilder builder = aKontaktregisterTo()
				.reservasjon(dki.isReservert());
		
		if (dki.getEpostadresse() != null) {
			builder.epostadresse(dki.getEpostadresse().trim());
		}
		if (dki.getMobiltelefonnummer() != null) {
			builder.mobiltelefonnummer(dki.getMobiltelefonnummer().trim());
		}
		
		return builder.build();
	}
	
	
	boolean mapStringToBool(String bool) {
		if (StringUtils.isBlank(bool)) {
			return true;
		}
		switch (bool.toLowerCase()) {
			case "ja":
			case "true":
				return true;
			default:
				return false;
		}
	}
}
