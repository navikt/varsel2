package no.nav.varsel.consumer.dkif.support;

import no.nav.varsel.consumer.dkif.DigitalKontaktInfoResponse;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static no.nav.varsel.consumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;


/**
 * Map response from HentDigitalKontaktinformasjon
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class HentDigitalKontaktinformasjonMapper {

	public KontaktregisterTo map(DigitalKontaktInfoResponse.DigitalKontaktinfo dki) {


		KontaktregisterTo.KontaktregisterToBuilder builder = aKontaktregisterTo()
				.reservasjon(dki.isReservert());

		if (dki.getEpostadresse() != null) {
			builder.epostadresse(dki.getEpostadresse().trim())
					.epostSistOppdatert(dki.getEpostadresseOppdatert()!=null?dki.getEpostadresseOppdatert().toLocalDateTime():null);
		}
		if (dki.getMobiltelefonnummer() != null) {
			builder.mobiltelefonnummer(dki.getMobiltelefonnummer().trim())
					.mobiltelefonSistOppdatert(dki.getMobiltelefonnummerOppdatert()!=null?dki.getMobiltelefonnummerOppdatert().toLocalDateTime():null);
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
