package no.nav.varsel.service.tvarsel001.support;

import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.doknotifikasjon.schemas.PrefererteKanal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static no.nav.varsel.domain.code.KanalCode.SMS;

@Component
public class EksternnotifikasjonMapper {

	private static final Integer SIKKERHETSNIVAA = 3;

	private final String applicationName;

	@Autowired
	public EksternnotifikasjonMapper(@Value("${applicationName}") String applicationName) {
		this.applicationName = applicationName;
	}

	public Doknotifikasjon mapDoknotifikasjon(Varselbestilling varselbestilling,
											  VarselutsendingTo varselutsendingTo,
											  VarselInfoTo varselInfoTo) {

		return Doknotifikasjon.newBuilder()
				.setBestillingsId(varselbestilling.getVarselbestillingId())
				.setBestillerId(applicationName)
				.setFodselsnummer(varselbestilling.getFnr())
				.setTittel(mapTittel(varselutsendingTo.getKanal(), varselInfoTo))
				.setEpostTekst(varselutsendingTo.getVarselTekst())
				.setSmsTekst(varselutsendingTo.getVarselTekst())
				.setPrefererteKanaler(Stream.of(mapKanal(varselutsendingTo.getKanal())).toList())
				.setSikkerhetsnivaa(SIKKERHETSNIVAA)
				.build();
	}

	private PrefererteKanal mapKanal(KanalCode kanalCode) {
		return PrefererteKanal.valueOf(kanalCode.name());
	}

	private String mapTittel(KanalCode kanalCode, VarselInfoTo varselInfoTo) {
		return SMS.equals(kanalCode) ? "SMS fra NAV" : varselInfoTo.getMal(kanalCode).getTittel();
	}

}
