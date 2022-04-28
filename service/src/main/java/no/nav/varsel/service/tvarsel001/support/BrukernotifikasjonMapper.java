package no.nav.varsel.service.tvarsel001.support;

import lombok.extern.slf4j.Slf4j;
import no.nav.brukernotifikasjon.schemas.builders.BeskjedInputBuilder;
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.support.VarselutsendingTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class BrukernotifikasjonMapper {

	private static final String NAMESPACE = "teamdokumenthandtering";
	private static final String APPNAVN = "varsel";
	private static final Integer SIKKERHETSNIVAA = 3;

	private final Clock clock;

	public BrukernotifikasjonMapper() {
		this.clock = Clock.systemDefaultZone();
	}

	public NokkelInput mapNokkel(Varselbestilling varselbestilling) {
		log.info("BrukernotifikasjonMapper - map nokkel: eventId={}, grupperingsId={}, fodselsnummer={}, namespace={}, appnavn={}",
				varselbestilling.getVarselbestillingId(),
				varselbestilling.getVarselbestillingId(),
				varselbestilling.getFnr(),
				NAMESPACE,
				APPNAVN);

		return new NokkelInputBuilder()
				.withEventId(varselbestilling.getVarselbestillingId())
				.withGrupperingsId(varselbestilling.getVarselbestillingId())
				.withFodselsnummer(varselbestilling.getFnr())
				.withNamespace(NAMESPACE)
				.withAppnavn(APPNAVN)
				.build();
	}

	public BeskjedInput mapBeskjed(VarselInfoTo varselInfoTo,
								   VarselutsendingTo varselutsendingTo) {
		log.info("BrukernotifikasjonMapper - map beskjed: tidspunkt={}, tekst={}, link={}, sikkerhetsnivaa={}",
				LocalDateTime.now(clock).atZone(ZoneId.systemDefault()).toLocalDateTime(),
				varselutsendingTo.getVarselTekst(),
				mapLink(varselInfoTo.getVarselUrl()),
				SIKKERHETSNIVAA);

		return new BeskjedInputBuilder()
				.withTidspunkt(LocalDateTime.now(clock).atZone(ZoneId.systemDefault()).toLocalDateTime())
				.withTekst(varselutsendingTo.getVarselTekst())
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
