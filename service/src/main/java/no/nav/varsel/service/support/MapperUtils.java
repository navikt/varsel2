package no.nav.varsel.service.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import java.util.List;

import static java.util.Collections.singletonList;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;

public class MapperUtils {

	public static List<PrefererteKanal> mapKanalToSingletonList(KanalCode kanalCode) {
		return singletonList(PrefererteKanal.valueOf(kanalCode.name()));
	}

	public static String mapTittel(KanalCode kanalCode, VarselInfoTo varselInfoTo) {
		return SMS.equals(kanalCode) ? "SMS fra NAV" : varselInfoTo.getMal(kanalCode).getTittel();
	}

	public static String mapTittel(List<VarselutsendingTo> varselutsendingToList, VarselInfoTo varselInfoTo) {

		return varselutsendingToList.stream()
				.filter(it -> EPOST.equals(it.getKanal()))
				.map(it -> varselInfoTo.getMal(EPOST).getTittel())
				.findAny()
				.orElse("SMS fra NAV");

	}

	public static String mapTekst(List<VarselutsendingTo> varselutsendingToList, KanalCode kanalCode) {

		return varselutsendingToList.stream()
				.filter(it -> kanalCode.equals(it.getKanal()))
				.map(VarselutsendingTo::getVarselTekst)
				.findAny()
				.orElse(null);
	}

	public static List<PrefererteKanal> mapKanaler(List<VarselutsendingTo> varselutsendingToList) {
		return varselutsendingToList.stream()
				.filter(it -> List.of(EPOST, SMS).contains(it.getKanal()))
				.map(it -> PrefererteKanal.valueOf(it.getKanal().name()))
				.toList();
	}

}
