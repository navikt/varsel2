package no.nav.varsel.wsconsumer.dkif.support;

import static no.nav.varsel.domain.auxillary.XmlGregorianConverter.toLocalDateTime;
import static no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.Kontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import org.apache.commons.lang3.StringUtils;


/**
 * Map response from HentDigitalKontaktinformasjon
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HentDigitalKontaktinformasjonMapper {

	public KontaktregisterTo map(HentDigitalKontaktinformasjonResponse response) {
		Kontaktinformasjon dki = response.getDigitalKontaktinformasjon();

		KontaktregisterTo.KontaktregisterToBuilder builder = aKontaktregisterTo()
				.personIdent(dki.getPersonident())
				.reservasjon(mapStringToBool(dki.getReservasjon()));

		if (dki.getEpostadresse() != null) {
			builder.epostadresse(dki.getEpostadresse().getValue())
					.epostSistOppdatert(toLocalDateTime(dki.getEpostadresse().getSistOppdatert()))
					.epostSistVerifisert(toLocalDateTime(dki.getEpostadresse().getSistVerifisert()));
		}
		if (dki.getMobiltelefonnummer() != null) {
			builder.mobiltelefonnummer(dki.getMobiltelefonnummer().getValue())
					.mobiltelefonSistOppdatert(toLocalDateTime(dki.getMobiltelefonnummer().getSistOppdatert()))
					.mobiltelefonSistVerifisert(toLocalDateTime(dki.getMobiltelefonnummer().getSistVerifisert()));
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
