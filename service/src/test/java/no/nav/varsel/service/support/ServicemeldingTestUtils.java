package no.nav.varsel.service.support;

import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;

public class ServicemeldingTestUtils {

	public static final String VARSELBESTILLINGS_ID = UUID.randomUUID().toString();
	public static final String FNR = "12345678910";
	public static final String NAMESPACE = "teamdokumenthandtering";
	public static final String APPNAVN = "varsel";
	public static final Integer SIKKERHETSNIVAA = 3;
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


	public static Varselbestilling createVarselbestilling() {
		var varselbestilling = new Varselbestilling();

		varselbestilling.setVarselbestillingId(VARSELBESTILLINGS_ID);
		varselbestilling.setFnr(FNR);

		return varselbestilling;
	}

	public static VarselInfoTo createVarselInfoTo() {
		return VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo()
				.varselUrl(VARSEL_URL)
				.maler(createMaler())
				.build();
	}

	public static VarselInfoTo createVarselInfoToWithInvalidUrl() {
		return VarselInfoTo.VarselInfoToBuilder.aVarselInfoTo()
				.varselUrl(UGYLDIG_VARSEL_URL)
				.build();
	}

	public static Varselutsending createVarselutsending(KanalCode kanalCode) {
		return Varselutsending.builder()
				.varselTekst(utledVarseltekst(kanalCode))
				.kanal(kanalCode)
				.varselTittel(utledTittel(kanalCode))
				.build();
	}

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


	public static List<Varselutsending> createVarselutsendingForKanaler(List<KanalCode> kanaler) {
		return kanaler.stream().map(ServicemeldingTestUtils::createVarselutsending).toList();
	}

	public static Set<VarselMalTo> createMaler() {

		return Stream.of(
						VarselMalTo.VarselMalToBuilder.aVarselMalTo()
								.foerstegangsTekst("Førstegangstekst epost")
								.revarslingTekst("Revarslingstekst epost")
								.kanal(EPOST)
								.tittel("Epost-tittel")
								.build(),
						VarselMalTo.VarselMalToBuilder.aVarselMalTo()
								.foerstegangsTekst("Førstegangstekst ditt nav")
								.revarslingTekst("Revarslingstekst ditt nav")
								.kanal(DITT_NAV)
								.tittel("Ditt Nav tittel")
								.build())
				.collect(Collectors.toSet());
	}

	public static Set<VarselMalTo> createDittNavMalUtenFoerstegangstekst() {
		return Stream.of(
				VarselMalTo.VarselMalToBuilder.aVarselMalTo()
						.foerstegangsTekst(null)
						.revarslingTekst("Revarslingstekst epost")
						.kanal(DITT_NAV)
						.tittel(null)
						.build()
		).collect(Collectors.toSet());
	}

	public static Varselutsending createVarselutsendingWithKanal(KanalCode kanalCode) {

		return Varselutsending.builder()
				.kanal(kanalCode)
				.build();
	}

	public static Doknotifikasjon createDoknotifikasjonWithKanalAndBestillingsId(KanalCode kanalCode, String bestillingsId) {
		return Doknotifikasjon.newBuilder()
				.setBestillingsId(bestillingsId)
				.setBestillerId(APPNAVN)
				.setSikkerhetsnivaa(3)
				.setFodselsnummer(FNR)
				.setTittel(utledTittel(kanalCode))
				.setEpostTekst(VARSELTEKST_EPOST)
				.setSmsTekst(VARSELTEKST_SMS)
				.setPrefererteKanaler(
						kanalCode == null ? List.of() : List.of(PrefererteKanal.valueOf(kanalCode.name())))
				.build();
	}

	public static NokkelInput createNokkelInputWithBestillingsId(String bestillingsId) {
		return new NokkelInputBuilder()
				.withEventId(bestillingsId)
				.withGrupperingsId(bestillingsId)
				.withFodselsnummer(FNR)
				.withNamespace(NAMESPACE)
				.withAppnavn(APPNAVN)
				.build();
	}

	public static BestillVarselTo createBestillVarselTo() {
		var bestillVarselTo = new BestillVarselTo();
		bestillVarselTo.setMobiltelefonnummer(MOBILNUMMER);
		bestillVarselTo.setEpost(EPOSTADRESSE);
		return bestillVarselTo;
	}

}
