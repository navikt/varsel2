package no.nav.varsel.service.tvarsel001.support;

import static java.util.stream.Collectors.toMap;
import static no.nav.varsel.domain.builder.VarselBuilder.aVarsel;
import static no.nav.varsel.domain.builder.VarselbestillingBuilder.aVarselbestilling;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.tvarsel001.to.BestillServicemeldingTo;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Mapper for TVARSEL001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ServicemeldingDomainMapper {

	@Inject
	private VarselFletter varselFletter;

	public Varselbestilling mapToDomain(BestillServicemeldingTo bestillServicemeldingTo,
										VarselInfoTo varselInfoTo,
										KontaktregisterTo kontaktregisterTo) {

		Varselbestilling varselbestilling = aVarselbestilling()
				.varselbestillingId(UUID.randomUUID().toString())
				.varslingstype(bestillServicemeldingTo.getVarslingstype())
				.preferertKanal(varselInfoTo.getPreferertKanal())
				.utlopTidspunkt(bestillServicemeldingTo.getUtloepstidspunkt())
				.fnr(bestillServicemeldingTo.getPersonIdent())
				.aktorId(bestillServicemeldingTo.getAktoerId())
				.bestillingTidspunkt(LocalDateTime.now())
				.revarslingIntervall(null)
				.antallRevarslinger(null)
				.nesteVarslingstidspunkt(null)
				.parameters(bestillServicemeldingTo.getParameters().entrySet().stream()
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
				.build();

		kontaktregisterTo.getKanaler().stream()
				.map((kanalCode) -> map(kanalCode, bestillServicemeldingTo, varselInfoTo, kontaktregisterTo))
				.forEach(varselbestilling::addVarsel);

		return varselbestilling;
	}

	private Varsel map(KanalCode kanalCode,
					   BestillServicemeldingTo bestillServicemeldingTo,
					   VarselInfoTo varselInfoTo,
					   KontaktregisterTo kontaktregisterTo) {

		VarselMalTo mal = varselInfoTo.getMal(kanalCode);
		String kontaktInfo =
				kanalCode == KanalCode.SMS ? kontaktregisterTo.getMobiltelefonnummer() :
						kanalCode == KanalCode.EPOST ? kontaktregisterTo.getEpostadresse() :
								"DITTNAV";

		return aVarsel()
				.varselId(UUID.randomUUID().toString())
				.kanal(kanalCode)
				.sendtTidspunkt(LocalDateTime.now())
				.distribusjonTidspunkt(null)
				.kontaktInfo(kontaktInfo)
				.status(StatusCode.SENDT)
				.feilbeskrivelse(null)
				.varselTittel(mal.getTittel())
				.varselTekst(varselFletter.flettVarsel(mal.getFoerstegangsTekst(), bestillServicemeldingTo.getParameters()))
				.varselUrl(varselInfoTo.getVarselURL())
				.build();
	}

}
