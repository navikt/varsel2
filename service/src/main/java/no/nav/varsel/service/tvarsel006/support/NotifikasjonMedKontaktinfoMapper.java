package no.nav.varsel.service.tvarsel006.support;

import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.Varselutsending;
import no.nav.varsel.service.to.BestillVarselTo;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.MapperUtils.mapKanaler;
import static no.nav.varsel.service.support.MapperUtils.mapTekst;
import static no.nav.varsel.service.support.MapperUtils.mapTittel;

@Slf4j
public class NotifikasjonMedKontaktinfoMapper {

	public NotifikasjonMedkontaktInfo mapNotifikasjonMedKontaktinfo(
			List<Varselutsending> varselutsendingList,
			Varselbestilling varselbestilling,
			BestillVarselTo bestillVarselTo
	) {

		return NotifikasjonMedkontaktInfo.newBuilder()
				.setBestillingsId(varselbestilling.getVarselbestillingId())
				.setBestillerId("varsel")
				.setFodselsnummer(varselbestilling.getFnr())
				.setMobiltelefonnummer(bestillVarselTo.getMobiltelefonnummer())
				.setEpostadresse(bestillVarselTo.getEpost())
				.setAntallRenotifikasjoner(0)
				.setRenotifikasjonIntervall(0)
				.setTittel(mapTittel(varselutsendingList))
				.setEpostTekst(mapTekst(varselutsendingList, EPOST))
				.setSmsTekst(mapTekst(varselutsendingList, SMS))
				.setPrefererteKanaler(mapKanaler(varselutsendingList))
				.build();
	}

}
