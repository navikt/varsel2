package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.brukernotifikasjon.schemas.builders.BeskjedInputBuilder;
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder;
import no.nav.brukernotifikasjon.schemas.builders.domain.PreferertKanal;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.varsel.config.VarselProperties;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.exception.functional.ServicemeldingMappingException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Component
public class BrukernotifikasjonMapper {

	private static final String NAMESPACE = "teamdokumenthandtering";
	private static final Integer SIKKERHETSNIVAA = 3;

	private final VarselProperties varselProperties;

	public BrukernotifikasjonMapper(VarselProperties varselProperties) {
		this.varselProperties = varselProperties;
	}

	public NokkelInput mapNokkel(Varselbestilling varselbestilling) {

		try {
			return new NokkelInputBuilder()
					.withEventId(varselbestilling.getVarselbestillingId())
					.withGrupperingsId(varselbestilling.getVarselbestillingId())
					.withFodselsnummer(varselbestilling.getFnr())
					.withNamespace(NAMESPACE)
					.withAppnavn(varselProperties.getAppName())
					.build();
		} catch (Exception e) {
			throw new ServicemeldingMappingException(e.getMessage(), e.getCause());
		}
	}

	public BeskjedInput mapBeskjed(List<Varselutsending> varselutsendingList) {

		var dittNavVarsel = varselutsendingList.stream()
				.filter(varsel -> varsel.getKanal().equals(KanalCode.DITT_NAV))
				.findFirst()
				.orElseThrow(() -> new ServicemeldingMappingException("Varselbestilling mangler DITT_NAV varsel"));

		var smsVarsel = varselutsendingList.stream()
				.filter(varsel -> varsel.getKanal().equals(KanalCode.SMS))
				.findFirst()
				.orElse(null);

		var epostVarsel = varselutsendingList.stream()
				.filter(varsel -> varsel.getKanal().equals(KanalCode.EPOST))
				.findFirst()
				.orElse(null);

		boolean eksternVarsling = smsVarsel != null || epostVarsel != null;

		try {
			var builder = new BeskjedInputBuilder()
					.withTidspunkt(LocalDateTime.now(UTC))
					.withTekst(dittNavVarsel.getVarselTekst())
					.withSikkerhetsnivaa(SIKKERHETSNIVAA)
					.withEksternVarsling(eksternVarsling);

			if (smsVarsel != null) {
				builder = builder
						.withSmsVarslingstekst(smsVarsel.getVarselTekst())
						.withPrefererteKanaler(PreferertKanal.SMS);
			}

			if (epostVarsel != null) {
				builder = builder
						.withEpostVarslingstittel(epostVarsel.getVarselTittel())
						.withEpostVarslingstekst(epostVarsel.getVarselTekst());
			}

			if (dittNavVarsel.getVarselUrl() != null) {
				builder = builder.withLink(mapLink(dittNavVarsel.getVarselUrl()));
			}

			return builder.build();
		} catch (Exception e) {
			throw new ServicemeldingMappingException(e.getMessage(), e.getCause());
		}
	}

	private URL mapLink(String varselUrl) {
		try {
			return UriComponentsBuilder.fromUriString(varselUrl).build().toUri().toURL();
		} catch (MalformedURLException | IllegalArgumentException e) {
			throw new RuntimeException(String.format("Ugyldig URL i varselbestilling. URL=%s", varselUrl), e.getCause());
		}
	}
}
