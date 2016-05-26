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
import no.nav.varsel.wsconsumer.dkif.to.DigitalKontaktinfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
										DigitalKontaktinfoTo digitalKontaktinfoTo) {

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

		digitalKontaktinfoTo.getKanaler().stream()
				.map((kanalCode) -> map(kanalCode, bestillServicemeldingTo, varselInfoTo, digitalKontaktinfoTo))
				.forEach(varselbestilling::addVarsel);

		return varselbestilling;
	}

	private Varsel map(KanalCode kanalCode,
					   BestillServicemeldingTo bestillServicemeldingTo,
					   VarselInfoTo varselInfoTo,
					   DigitalKontaktinfoTo digitalKontaktinfoTo) {

		VarselMalTo mal = varselInfoTo.getMal(kanalCode);
		String kontaktInfo =
				kanalCode == KanalCode.SMS ? digitalKontaktinfoTo.getMobiltelefonnummer() :
						kanalCode == KanalCode.EPOST ? digitalKontaktinfoTo.getEpostadresse() :
								"DITTNAV";

		return aVarsel()
				.varselId(UUID.randomUUID().toString())
				.kanal(kanalCode)
				.sendtTidspunkt(null)
				.distribusjonTidspunkt(null)
				.kontaktInfo(kontaktInfo)
				.status(StatusCode.OPPRETTET)
				.feilbeskrivelse(null)
				.varselTittel(mal.getTittel())
				.varselTekst(varselFletter.flettVarsel(mal.getFoerstegangsTekst(), bestillServicemeldingTo.getParameters()))
				.varselUrl(varselInfoTo.getVarselURL())
				.build();
	}

	public Collection<KanalCode> decideKanaler(DigitalKontaktinfoTo digitalKontaktinfoTo, VarselInfoTo varselInfoTo) {
		Set<KanalCode> kanaler = new HashSet<>();
		String preferertKanal = varselInfoTo.getPreferertKanal();

		if (preferertKanal.equals(KanalCode.DITTNAV.toString())) {
			kanaler.add(KanalCode.DITTNAV);
//			return kanaler;
		}

		if (preferertKanal.equals(KanalCode.SMS.toString() + "_" + KanalCode.EPOST.toString())) {

		}


		return kanaler;
	}
}
