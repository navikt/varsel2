package no.nav.varsel.domain.code;

/**
 * Kodeverdi for Kanal
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public enum KanalCode {
	SMS,
	EPOST("EPST"),
	DITT_NAV("NAV_NO");

	private String kommunikasjonskanal;

	KanalCode() {
		this.kommunikasjonskanal = this.name();
	}

	/**
	 * @param kommunikasjonskanal felles kodeverk for kommunikasjonskanal mot KES
	 */
	KanalCode(String kommunikasjonskanal) {
		this.kommunikasjonskanal = kommunikasjonskanal;
	}

	/**
	 * @return kommunikasjonskanal felles kodeverk for kommunikasjonskanal mot KES
	 */
	public String getKommunikasjonskanal() {
		return kommunikasjonskanal;
	}

	public boolean hasExternalUtsendingskanal() {
		return this != DITT_NAV;
	}
}
