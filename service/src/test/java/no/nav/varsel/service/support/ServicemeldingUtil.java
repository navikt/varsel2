package no.nav.varsel.service.support;

import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;

public class ServicemeldingUtil {

	public static final String VARSELBESTILLINGS_ID = UUID.randomUUID().toString();
	public static final String FNR = "12345678910";
	public static final String NAMESPACE = "teamdokumenthandtering";
	public static final String APPNAVN = "varsel";
	public static final Integer SIKKERHETSNIVAA = 3;
	public static final String VARSEL_URL = "https://www.varsel.com";
	public static final String UGYLDIG_VARSEL_URL = "httpp://www.invalidurl.com";
	public static final String VARSELTEKST = "Tekst i varselet";
	public static final String VARSELTYPE_ID = "NySykemelding";
	public static final String VARSELTITTEL = "Epost tittel";

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

	public static VarselutsendingTo createVarselutsending() {
		return VarselutsendingTo.builder()
				.varselTekst(VARSELTEKST)
				.kanal(EPOST)
				.varselTittel(VARSELTITTEL)
				.build();
	}

	public static Set<VarselMalTo> createMaler() {

		return Stream.of(
						VarselMalTo.VarselMalToBuilder.aVarselMalTo()
								.foerstegangsTekst("Førstegangstekst epost")
								.revarslingTekst("Revarslingstekst epost")
								.kanal(EPOST)
								.tittel("Epost tittel")
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

	public static VarselutsendingTo createVarselutsendingToWithKanal(KanalCode kanalCode) {

		return VarselutsendingTo.builder()
				.kanal(kanalCode)
				.build();
	}

	public static Doknotifikasjon createDoknotifikasjonWithKanalAndBestillingsId(KanalCode kanalCode, String bestillingsId) {
		return Doknotifikasjon.newBuilder()
				.setBestillingsId(bestillingsId)
				.setBestillerId("varsel")
				.setSikkerhetsnivaa(3)
				.setFodselsnummer("12345678910")
				.setTittel("tittel")
				.setEpostTekst("Epost tekst")
				.setSmsTekst("Sms tekst")
				.setPrefererteKanaler(
						kanalCode == null ? List.of() : List.of(PrefererteKanal.valueOf(kanalCode.name())))
				.build();
	}

	public static NokkelInput createNokkelInputWithBestillingsId(String bestillingsId) {
		return new NokkelInputBuilder()
				.withEventId(bestillingsId)
				.withGrupperingsId(bestillingsId)
				.withFodselsnummer("12345678910")
				.withNamespace("teamdokumenthandtering")
				.withAppnavn("varsel")
				.build();
	}
}
