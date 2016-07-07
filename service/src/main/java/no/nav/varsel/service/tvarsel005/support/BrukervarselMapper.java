package no.nav.varsel.service.tvarsel005.support;

import static java.util.stream.Collectors.toList;
import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo.Builder.aHentVarselForBrukerResponseTo;
import static no.nav.varsel.service.tvarsel005.to.VarselTo.Builder.aVarselTo;
import static no.nav.varsel.service.tvarsel005.to.VarselbestillingTo.Builder.aVarselbestillingTo;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;

import java.util.List;

/**
 * Service mapper fro Tvarsel005 hent bruker varsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BrukervarselMapper {

	public HentVarselForBrukerResponseTo map(List<Varselbestilling> varselbestillings) {
		return aHentVarselForBrukerResponseTo()
				.varselbestillingTos(varselbestillings.stream().map(this::mapVarselbestilling)
						.filter(vb -> !vb.getVarsler().isEmpty()).collect(toList()))
				.build();
	}

	private VarselbestillingTo mapVarselbestilling(Varselbestilling varselbestilling) {
		return aVarselbestillingTo()
				.varseltypeId(varselbestilling.getVarseltypeId())
				.varsler(varselbestilling.getVarsels().stream().map(this::mapVarsel).filter(v -> v != null).collect(toList()))
				.fnr(varselbestilling.getFnr())
				.aktoerId(varselbestilling.getAktorId())
				.bestillingstidspunkt(varselbestilling.getBestillingTidspunkt())
				.revarslingIntervall(varselbestilling.getRevarslingIntervall())
				.buildAndCalculateSisteVarselUtsendelse();
	}

	private VarselTo mapVarsel(Varsel varsel) {
		if (varsel.getStatus() != StatusCode.FERDIGBEHANDLET) {
			return null;
		}
		return aVarselTo()
				.kanal(varsel.getKanal().toString())
				.sendtTidspunkt(varsel.getSendtTidspunkt())
				.distribusjonTidspunkt(varsel.getDistribusjonTidspunkt())
				.kontaktInfo(varsel.getKontaktInfo())
				.varselTittel(varsel.getVarselTittel())
				.varselTekst(varsel.getVarselTekst())
				.varselURL(varsel.getVarselUrl())
				.revarsel(varsel.getErRevarsel())
				.build();
	}
}
