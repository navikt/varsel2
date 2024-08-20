package no.nav.varsel.service.support;

import com.google.common.collect.Maps;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.to.Varselinfo;
import no.nav.varsel.consumer.dokmet.to.Varselmal;
import no.nav.varsel.domain.builder.VarselbestillingBuilder;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.VarselFletter;
import no.nav.varsel.service.support.exception.functional.VarselTekstMissingException;
import no.nav.varsel.service.to.BestillVarselTo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static no.nav.varsel.domain.builder.VarselBuilder.aVarsel;
import static no.nav.varsel.domain.builder.VarselbestillingBuilder.aVarselbestilling;

public class VarselBestillingDomainMapper {

	@Autowired
	private VarselFletter varselFletter;

	public Varselbestilling mapVarselbestillingFoerstegangVarselMedRevarsel(BestillVarselTo bestillingTo,
			Varselinfo varselinfo,
			KontaktregisterTo kontaktregisterTo) {
		return mapVarselbestilling(bestillingTo, varselinfo, kontaktregisterTo, true);
	}

	public Varselbestilling mapVarselbestillingFoerstegangVarselUtenRevarsel(BestillVarselTo bestillingTo,
			Varselinfo varselinfo,
			KontaktregisterTo kontaktregisterTo) {
		return mapVarselbestilling(bestillingTo, varselinfo, kontaktregisterTo, false);
	}

	public Varsel mapReVarsel(KanalCode kanalCode,
			BestillVarselTo bestillVarselTo,
			Varselinfo varselinfo,
			KontaktregisterTo kontaktregisterTo) {
		return mapVarsel(kanalCode, bestillVarselTo, varselinfo, kontaktregisterTo, true);
	}

	private Varselbestilling mapVarselbestilling(BestillVarselTo bestillingTo,
			Varselinfo varselinfo,
			KontaktregisterTo kontaktregisterTo,
			boolean withRevarsel) {

		VarselbestillingBuilder builder = aVarselbestilling()
				.varselbestillingId(bestillingTo.getVarselBestillingId())
				.varseltypeId(bestillingTo.getVarseltypeId())
				.utlopTidspunkt(bestillingTo.getUtloepstidspunkt())
				.fnr(bestillingTo.getPersonIdent())
				.orgNr(bestillingTo.getOrgNr())
				.aktorId(bestillingTo.getAktoerId())
				.bestillingTidspunkt(LocalDateTime.now())
				.parameters(bestillingTo.getParameters().entrySet().stream()
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));

		if (withRevarsel && varselinfo.getRevarslingIntervall() != null && varselinfo.getAntallRevarsling() != null) {
			builder.revarslingIntervall(varselinfo.getRevarslingIntervall())
					.antallRevarslinger(varselinfo.getAntallRevarsling())
					.nesteVarslingDato(LocalDate.now().plusDays(varselinfo.getRevarslingIntervall()));
		}

		Varselbestilling varselbestilling = builder
				.build();

		kontaktregisterTo.getKanaler().stream()
				.map((kanalCode) -> mapVarsel(kanalCode, bestillingTo, varselinfo, kontaktregisterTo))
				.forEach(varselbestilling::addVarsel);

		return varselbestilling;
	}

	private Varsel mapVarsel(KanalCode kanalCode,
			BestillVarselTo bestillVarselTo,
			Varselinfo varselinfo,
			KontaktregisterTo kontaktregisterTo) {
		return mapVarsel(kanalCode, bestillVarselTo, varselinfo, kontaktregisterTo, false);
	}

	private Varsel mapVarsel(KanalCode kanalCode,
			BestillVarselTo bestillVarselTo,
			Varselinfo varselinfo,
			KontaktregisterTo kontaktregisterTo,
			boolean revarsel) {

		Varselmal mal = varselinfo.getMal(kanalCode);
		String kontaktInfo =
				kanalCode == KanalCode.SMS ? kontaktregisterTo.getMobiltelefonnummer() :
						kanalCode == KanalCode.EPOST ? kontaktregisterTo.getEpostadresse() :
								null;
		String tekstMal = revarsel ? mal.revarslingTekst() : mal.foerstegangsTekst();
		String varselId = UUID.randomUUID().toString();
		Map<String, String> params = Maps.newHashMap(bestillVarselTo.getParameters());
		String varselUrl = varselFletter.weaveText(varselinfo.getVarselUrl(), params);
		String varselTekst = varselFletter.weaveText(tekstMal, params);
		String varselTittel = varselFletter.weaveText(mal.tittel(), params);

		if (StringUtils.isEmpty(varselTekst)) {
			throw new VarselTekstMissingException(
					String.format("Missing varseltekst for varselbestillingsId=%s, kanalCode=%s", bestillVarselTo.getVarselBestillingId(), kanalCode));
		}

		return aVarsel()
				.varselId(varselId)
				.kanal(kanalCode)
				.sendtTidspunkt(LocalDateTime.now())
				.distribusjonTidspunkt(null)
				.kontaktInfo(kontaktInfo)
				.status(StatusCode.SENDT)
				.feilbeskrivelse(null)
				.varselTittel(varselTittel)
				.varselTekst(varselTekst)
				.varselUrl(kanalCode == KanalCode.DITT_NAV ? varselUrl : null)
				.erRevarsel(revarsel)
				.build();
	}
}
