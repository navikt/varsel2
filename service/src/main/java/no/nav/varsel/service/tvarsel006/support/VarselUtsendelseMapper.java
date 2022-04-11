package no.nav.varsel.service.tvarsel006.support;

import no.nav.doknotifikasjon.schemas.NotifikasjonMedkontaktInfo;
import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import no.nav.varsel.service.to.BestillVarselTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;

import java.util.stream.Stream;

import static no.nav.varsel.domain.code.KanalCode.SMS;

public class VarselUtsendelseMapper {

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
				.setPrefererteKanaler(Stream.of(mapKanal(varselutsendingTo.getKanal())).toList())
				.build();

	}

	private PrefererteKanal mapKanal(KanalCode kanalCode) {
		return PrefererteKanal.valueOf(kanalCode.name());
	}

	private String mapTittel(KanalCode kanalCode, VarselInfoTo varselInfoTo) {
		return SMS.equals(kanalCode) ? "Dummy tittel" : varselInfoTo.getMal(kanalCode).getTittel();
	}
}
