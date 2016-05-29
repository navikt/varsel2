package no.nav.varsel.wsconsumer.dkif;

import static no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;

import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import org.joda.time.LocalDateTime;

/**
 * HentDigitalKontaktinformasjon Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HentDigitalKontaktinformasjonConsumer {

	public KontaktregisterTo hentDigitalKontaktinformasjon(String personIdent) {
		KontaktregisterTo kontaktregisterTo = aKontaktregisterTo()
				.personIdent("***gammelt_fnr***")
				.reservasjon(false)
				.epostadresse("epost@epost.no")
				.epostSistOppdatert(LocalDateTime.now())
				.epostSistVerifisert(LocalDateTime.now())
				.mobiltelefonnummer("12345678")
				.mobiltelefonSistOppdatert(LocalDateTime.now())
				.mobiltelefonSistVerifisert(LocalDateTime.now())
				.build();
		kontaktregisterTo.cleanExpiredInfo();
		return kontaktregisterTo;
	}
}
