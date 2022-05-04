package no.nav.varsel.service.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;

import java.util.List;

import static java.util.Collections.singletonList;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;

public class MapperUtils {

	public static List<PrefererteKanal> mapKanalToSingletonList(KanalCode kanalCode) {
		return singletonList(PrefererteKanal.valueOf(kanalCode.name()));
	}

	public static String mapTittel(List<Varselutsending> varselutsendingList) {
		return varselutsendingList.stream()
				.filter(it -> EPOST.equals(it.getKanal()))
				.map(Varselutsending::getVarselTittel)
				.findAny()
				.orElse("SMS fra NAV");
	}

	public static String mapTekst(List<Varselutsending> varselutsendingList, KanalCode kanalCode) {
		return varselutsendingList.stream()
				.filter(it -> kanalCode.equals(it.getKanal()))
				.map(Varselutsending::getVarselTekst)
				.findAny()
				.orElse(null);
	}

	public static List<PrefererteKanal> mapKanaler(List<Varselutsending> varselutsendingList) {
		return varselutsendingList.stream()
				.filter(it -> List.of(EPOST, SMS).contains(it.getKanal()))
				.map(it -> PrefererteKanal.valueOf(it.getKanal().name()))
				.toList();
	}

}
