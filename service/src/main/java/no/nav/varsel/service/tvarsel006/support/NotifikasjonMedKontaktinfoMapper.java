package no.nav.varsel.service.tvarsel006.support;

import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.MapperUtils.mapKanaler;
import static no.nav.varsel.service.support.MapperUtils.mapTekst;
import static no.nav.varsel.service.support.MapperUtils.mapTittel;

@Slf4j
public class NotifikasjonMedKontaktinfoMapper {

	public NotifikasjonMedkontaktInfo mapNotifikasjonMedKontaktinfo(List<VarselutsendingTo> varselutsendingToList,
																	Varselbestilling varselbestilling,
																	BestillVarselTo bestillVarselTo,
																	VarselInfoTo varselInfoTo) {

		return NotifikasjonMedkontaktInfo.newBuilder()
				.setBestillingsId(varselbestilling.getVarselbestillingId())
				.setBestillerId("varsel")
				.setFodselsnummer(varselbestilling.getFnr())
				.setMobiltelefonnummer(bestillVarselTo.getMobiltelefonnummer())
				.setEpostadresse(bestillVarselTo.getEpost())
				.setAntallRenotifikasjoner(0)
				.setRenotifikasjonIntervall(0)
				.setTittel(mapTittel(varselutsendingToList, varselInfoTo))
				.setEpostTekst(mapTekst(varselutsendingToList, EPOST))
				.setSmsTekst(mapTekst(varselutsendingToList, SMS))
				.setPrefererteKanaler(mapKanaler(varselutsendingToList))
				.build();
	}

}
