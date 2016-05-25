package no.nav.varsel.wsconsumer.dkif;

import no.nav.varsel.wsconsumer.dkif.to.DigitalKontaktinfoTo;
import org.joda.time.LocalDateTime;

/**
 * HentDigitalKontaktinformasjon Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HentDigitalKontaktinformasjonConsumer {

	public DigitalKontaktinfoTo hentDigitalKontaktinformasjon(String personIdent) {
		DigitalKontaktinfoTo digitalKontaktinfoTo = new DigitalKontaktinfoTo();
		digitalKontaktinfoTo.setPersonident("***gammelt_fnr***");
		digitalKontaktinfoTo.setEpostadresse("epost@epost.no");
		digitalKontaktinfoTo.setEpostSistOppdatert(LocalDateTime.now());
		digitalKontaktinfoTo.setEpostSistVerifisert(LocalDateTime.now());
		digitalKontaktinfoTo.setMobiltelefonnummer("12345678");
		digitalKontaktinfoTo.setMobiltelefonSistOppdatert(LocalDateTime.now());
		digitalKontaktinfoTo.setMobiltelefonSistVerifisert(LocalDateTime.now());
		return digitalKontaktinfoTo;
	}
}
