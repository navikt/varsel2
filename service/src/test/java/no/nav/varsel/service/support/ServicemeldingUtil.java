package no.nav.varsel.service.support;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import java.util.UUID;

public class ServicemeldingUtil {

	public static final String VARSELBESTILLINGS_ID = UUID.randomUUID().toString();
	public static final String FNR = "12345678910";
	public static final String NAMESPACE = "teamdokumenthandtering";
	public static final String APPNAVN = "varsel";
	public static final Integer SIKKERHETSNIVAA = 3;
	public static final String VARSEL_URL = "https://www.varsel.com";
	public static final String UGYLDIG_VARSEL_URL = "httpp://www.invalidurl.com";
	public static final String VARSELTEKST = "Tekst i varselet";

	public static Varselbestilling createVarselbestilling() {
		var varselbestilling = new Varselbestilling();

		varselbestilling.setVarselbestillingId(VARSELBESTILLINGS_ID);
		varselbestilling.setFnr(FNR);

		return varselbestilling;
	}

	public static VarselInfoTo createVarselInfoTo() {
		return VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo()
				.varselUrl(VARSEL_URL)
				.build();
	}

	public static VarselInfoTo createVarselInfoToWithInvalidUrl() {
		return VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo()
				.varselUrl(UGYLDIG_VARSEL_URL)
				.build();
	}

	public static VarselutsendingTo createVarselutsending() {
		return VarselutsendingTo.VarselutsendingToBuilder.aVarselutsendingTo()
				.varselTekst(VARSELTEKST)
				.build();
	}

}
