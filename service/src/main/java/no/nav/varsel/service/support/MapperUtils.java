package no.nav.varsel.service.support;

import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.SMS;

public class MapperUtils {

	public static List<PrefererteKanal> mapKanalToSingletonList(KanalCode kanalCode) {
		return singletonList(PrefererteKanal.valueOf(kanalCode.name()));
	}

	public static String mapTittel(KanalCode kanalCode, VarselInfoTo varselInfoTo) {
		return SMS.equals(kanalCode) ? "SMS fra NAV" : varselInfoTo.getMal(kanalCode).getTittel();
	}
}
