package no.nav.varsel.service.tvarsel001.support;

import lombok.extern.slf4j.Slf4j;
import no.nav.brukernotifikasjon.schemas.builders.BeskjedInputBuilder;
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.Varselutsending;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Component
public class BrukernotifikasjonMapper {

	private static final String NAMESPACE = "teamdokumenthandtering";
	private static final Integer SIKKERHETSNIVAA = 3;

	private final String applicationName;

	public BrukernotifikasjonMapper(@Value("${applicationName}") String applicationName) {
		this.applicationName = applicationName;
	}

	public NokkelInput mapNokkel(Varselbestilling varselbestilling) {

		return new NokkelInputBuilder()
				.withEventId(varselbestilling.getVarselbestillingId())
				.withGrupperingsId(varselbestilling.getVarselbestillingId())
				.withFodselsnummer(varselbestilling.getFnr())
				.withNamespace(NAMESPACE)
				.withAppnavn(applicationName)
				.build();
	}

	public BeskjedInput mapBeskjed(VarselInfoTo varselInfoTo,
								   Varselutsending varselutsending) {

		return new BeskjedInputBuilder()
				.withTidspunkt(LocalDateTime.now(UTC))
				.withTekst(varselutsending.getVarselTekst())
				.withLink(mapLink(varselInfoTo.getVarselUrl()))
				.withSikkerhetsnivaa(SIKKERHETSNIVAA)
				.withEksternVarsling(false)
				.build();
	}

	private URL mapLink(String varselUrl) {
		try {
			return UriComponentsBuilder.fromHttpUrl(varselUrl).build().toUri().toURL();
		} catch (MalformedURLException | IllegalArgumentException e) {
			throw new RuntimeException(String.format("Ugyldig URL i varselbestilling. URL=%s", varselUrl), e.getCause());
		}
	}
}
