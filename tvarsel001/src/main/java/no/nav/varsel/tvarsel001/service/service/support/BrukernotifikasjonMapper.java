package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.tms.varsel.action.EksternKanal;
import no.nav.tms.varsel.action.Sensitivitet;
import no.nav.tms.varsel.action.Varseltype;
import no.nav.tms.varsel.builder.OpprettVarselBuilder;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.exception.functional.ServicemeldingMappingException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class BrukernotifikasjonMapper {

	private BrukernotifikasjonMapper() {}

	public static String mapAndMarshalOpprettVarsel(Varselbestilling varselbestilling) {

		List<Varselutsending> varselutsendingList = varselbestilling.getVarsels().stream()
				.map(varsel -> Varselutsending.builder()
						.kanal(varsel.getKanal())
						.varselUrl(varsel.getVarselUrl())
						.varselTekst(varsel.getVarselTekst())
						.varselTittel(varsel.getVarselTittel())
						.build()
				).toList();

		var dittNavVarsel = varselutsendingList.stream()
				.filter(varsel -> varsel.getKanal().equals(KanalCode.DITT_NAV))
				.findFirst()
				.orElseThrow(() -> new ServicemeldingMappingException("Varselbestilling mangler DITT_NAV varsel"));

		try {
			var builder = OpprettVarselBuilder.newInstance()
					.withType(Varseltype.Beskjed)
					.withVarselId(varselbestilling.getVarselbestillingId())
					.withSensitivitet(Sensitivitet.Substantial)
					.withIdent(varselbestilling.getFnr())
					.withTekst("nb", dittNavVarsel.getVarselTekst(), true)
					.withLink(dittNavVarsel.getVarselUrl())
					.withAktivFremTil(LocalDate.now().plusDays(10).atStartOfDay(ZoneOffset.UTC));

			var smsVarsel = varselutsendingList.stream()
					.filter(varsel -> varsel.getKanal().equals(KanalCode.SMS))
					.findFirst();

			var epostVarsel = varselutsendingList.stream()
					.filter(varsel -> varsel.getKanal().equals(KanalCode.EPOST))
					.findFirst();

			boolean eksternVarsling = smsVarsel.isPresent() || epostVarsel.isPresent();

			if (eksternVarsling)
					builder.withEksternVarsling(
							OpprettVarselBuilder.eksternVarsling()
									.withPreferertKanal(smsVarsel.isPresent() ? EksternKanal.SMS : EksternKanal.EPOST)
									.withSmsVarslingstekst(smsVarsel.map(Varselutsending::getVarselTekst).orElse(null))
									.withEpostVarslingstittel(epostVarsel.map(Varselutsending::getVarselTittel).orElse(null))
									.withEpostVarslingstekst(epostVarsel.map(Varselutsending::getVarselTekst).orElse(null))
					);

			return builder.build();
		} catch (Exception e) {
			throw new ServicemeldingMappingException(e.getMessage(), e.getCause());
		}
	}
}
