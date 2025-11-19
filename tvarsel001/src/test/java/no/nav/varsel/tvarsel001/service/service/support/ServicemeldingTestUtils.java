package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.varsel.consumer.dokmet.Varselmal;
import no.nav.varsel.domain.builder.VarselbestillingBuilder;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static no.nav.varsel.domain.builder.VarselBuilder.aVarsel;
import static no.nav.varsel.domain.builder.VarselbestillingBuilder.aVarselbestilling;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;

public class ServicemeldingTestUtils {

	public static final String VARSELBESTILLINGS_ID = UUID.randomUUID().toString();
	public static final String FNR = "12345678910";
	public static final String NAMESPACE = "teamdokumenthandtering";
	public static final String APPNAVN = "no/nav/varsel";
	public static final String BESTILLER_ID = "tms-ekstern-varsling";
	public static final String SIKKERHETSNIVAA_MINID = "substantial";
	public static final String VARSEL_URL = "https://www.varsel.com";
	public static final String UGYLDIG_VARSEL_URL = "httpp://www.invalidurl.com";
	public static final String VARSELTEKST_EPOST = "Tekst i epost-varsel";
	public static final String VARSELTEKST_SMS = "Tekst i sms-varsel";
	public static final String VARSELTEKST_DITT_NAV = "Tekst i Ditt NAV-varsel";
	public static final String VARSELTYPE_ID = "NySykemelding";
	public static final String VARSELTITTEL_EPOST = "Epost-tittel";
	public static final String VARSELTITTEL_SMS = "SMS fra NAV";
	public static final String VARSELTITTEL_DITT_NAV = "Ditt NAV-tittel";
	public static final String MOBILNUMMER = "12345678";
	public static final String EPOSTADRESSE = "epost@post.no";
	public static final String FOERSTEGANGS_TEKST = "foreste tekst for ";
	public static final String REVARSLING_TEKST = "revarsling tekst for ";
	public static final String TITTEL = "tittel";


	private static String utledVarseltekst(KanalCode kanalCode) {
		return switch (kanalCode) {
			case EPOST -> VARSELTEKST_EPOST;
			case SMS -> VARSELTEKST_SMS;
			case DITT_NAV -> VARSELTEKST_DITT_NAV;
		};
	}

	private static String utledTittel(KanalCode kanalCode) {
		if (kanalCode == null) {
			return "Tittel hvis kanal ikke eksisterer";
		}

		return switch (kanalCode) {
			case EPOST -> VARSELTITTEL_EPOST;
			case SMS -> VARSELTITTEL_SMS;
			case DITT_NAV -> VARSELTITTEL_DITT_NAV;
		};
	}

	public static Varselmal createDittNavMalUtenFoerstegangstekst() {
		return new Varselmal(DITT_NAV, null, null, "Revarslingstekst epost");
	}

	public static Varselmal createVarselmal(KanalCode kanalCode) {
		return new Varselmal(kanalCode, TITTEL, FOERSTEGANGS_TEKST, REVARSLING_TEKST);
	}

	public static Varselbestilling createFrom(KanalCode... kanalCodes) {
		return createFrom(VARSEL_URL, kanalCodes);
	}

	public static Varselbestilling createFrom(String varselUrl, KanalCode... kanalCodes) {
		VarselbestillingBuilder builder = aVarselbestilling()
				.varselbestillingId(VARSELBESTILLINGS_ID)
				.fnr(FNR);

		Varselbestilling varselbestilling = builder.build();

		Stream.of(kanalCodes)
				.map((KanalCode kanalCode) -> mapVarsel(kanalCode, varselUrl))
				.forEach(varselbestilling::addVarsel);

		return varselbestilling;
	}

	private static Varsel mapVarsel(KanalCode kanalCode, String varselUrl) {
		return aVarsel()
				.varselId(UUID.randomUUID().toString())
				.kanal(kanalCode)
				.sendtTidspunkt(LocalDateTime.now())
				.distribusjonTidspunkt(null)
				.status(StatusCode.SENDT)
				.feilbeskrivelse(null)
				.varselTittel(utledTittel(kanalCode))
				.varselTekst(utledVarseltekst(kanalCode))
				.varselUrl(kanalCode == KanalCode.DITT_NAV ? varselUrl : null)
				.erRevarsel(false)
				.build();
	}
}
