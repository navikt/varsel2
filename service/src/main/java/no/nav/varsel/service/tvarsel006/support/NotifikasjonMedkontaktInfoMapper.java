package no.nav.varsel.service.tvarsel006.support;

import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import static no.nav.varsel.service.support.MapperUtils.mapKanalToSingletonList;
import static no.nav.varsel.service.support.MapperUtils.mapTittel;

public class NotifikasjonMedkontaktInfoMapper {

	public NotifikasjonMedkontaktInfo mapNotifikasjonMedKontaktInfo(
			BestillVarselTo bestillVarselTo,
			Varselbestilling varselbestilling,
			VarselutsendingTo varselutsendingTo,
			VarselInfoTo varselInfoTo) {

		return NotifikasjonMedkontaktInfo.newBuilder()
				.setBestillingsId(varselbestilling.getVarselbestillingId())
				.setBestillerId("varsel")
				.setFodselsnummer(varselbestilling.getFnr())
				.setMobiltelefonnummer(bestillVarselTo.getMobiltelefonnummer())
				.setEpostadresse(bestillVarselTo.getEpost())
				.setAntallRenotifikasjoner(0)
				.setRenotifikasjonIntervall(0)
				.setTittel(mapTittel(varselutsendingTo.getKanal(), varselInfoTo))
				.setEpostTekst(varselutsendingTo.getVarselTekst())
				.setSmsTekst(varselutsendingTo.getVarselTekst())
				.setPrefererteKanaler(mapKanalToSingletonList(varselutsendingTo.getKanal()))
				.build();
	}


}
