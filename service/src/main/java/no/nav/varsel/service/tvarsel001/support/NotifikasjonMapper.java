package no.nav.varsel.service.tvarsel001.support;

import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.Varselutsending;
import no.nav.varsel.service.support.exception.functional.ServicemeldingMappingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.service.support.MapperUtils.mapKanaler;
import static no.nav.varsel.service.support.MapperUtils.mapTekst;
import static no.nav.varsel.service.support.MapperUtils.mapTittel;

@Component
@Slf4j
public class NotifikasjonMapper {

	private static final Integer SIKKERHETSNIVAA = 3;

	private final String applicationName;

	public NotifikasjonMapper(@Value("${applicationName}") String applicationName) {
		this.applicationName = applicationName;
	}

	public Doknotifikasjon mapNotifikasjon(List<Varselutsending> varselutsendingList,
										   Varselbestilling varselbestilling
	) {

		try {
			return Doknotifikasjon.newBuilder()
					.setBestillingsId(varselbestilling.getVarselbestillingId())
					.setBestillerId(applicationName)
					.setFodselsnummer(varselbestilling.getFnr())
					.setTittel(mapTittel(varselutsendingList))
					.setEpostTekst(mapTekst(varselutsendingList, EPOST))
					.setSmsTekst(mapTekst(varselutsendingList, SMS))
					.setPrefererteKanaler(mapKanaler(varselutsendingList))
					.setSikkerhetsnivaa(SIKKERHETSNIVAA)
					.build();
		} catch (Exception e) {
			throw new ServicemeldingMappingException(e.getMessage(), e.getCause());
		}
	}
}
