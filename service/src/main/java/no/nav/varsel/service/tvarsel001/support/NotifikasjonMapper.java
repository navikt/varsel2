package no.nav.varsel.service.tvarsel001.support;

import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static no.nav.varsel.service.support.MapperUtils.mapKanalToSingletonList;
import static no.nav.varsel.service.support.MapperUtils.mapTittel;

@Component
public class NotifikasjonMapper {

	private static final Integer SIKKERHETSNIVAA = 3;

	private final String applicationName;

	@Autowired
	public NotifikasjonMapper(@Value("${applicationName}") String applicationName) {
		this.applicationName = applicationName;
	}

	public Doknotifikasjon mapNotifikasjon(Varselbestilling varselbestilling,
										   VarselutsendingTo varselutsendingTo,
										   VarselInfoTo varselInfoTo) {

		return Doknotifikasjon.newBuilder()
				.setBestillingsId(varselbestilling.getVarselbestillingId())
				.setBestillerId(applicationName)
				.setFodselsnummer(varselbestilling.getFnr())
				.setTittel(mapTittel(varselutsendingTo.getKanal(), varselInfoTo))
				.setEpostTekst(varselutsendingTo.getVarselTekst())
				.setSmsTekst(varselutsendingTo.getVarselTekst())
				.setPrefererteKanaler(mapKanalToSingletonList(varselutsendingTo.getKanal()))
				.setSikkerhetsnivaa(SIKKERHETSNIVAA)
				.build();
	}


}
