package no.nav.varsel.provider.map.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.feil.UgydigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Periode;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.varsel.domain.utility.XmlGregorianConverter;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;

/**
 * Validator for MottaDigitalPostKvitteringRequest
 *
 * @author Lars Aune
 */
public class HentVarselForBrukerRequestValidator {

	public static final String PAAKREVD_INPUTPARAMETER_ER_IKKE_SATT_FEILMELDING = "Påkrevd inputparameter er ikke satt";
	public static final String INPUTPARAMETER_BRUKER_MANGLER_FEILAARSAK = "Inputparameter Bruker mangler";
	public static final String UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_FEILMELDING = "Ugyldig bruk av DatoFom og DatoTom";
	public static final String PERIODENS_TOM_KAN_IKKE_VÆRE_SENERE_ENN_DAGENS_DATO_FEILAARSAK = "Periodens DatoTom kan ikke være senere enn dagens dato";
	public static final String PERIODENS_DATO_FOM_KAN_IKKE_VÆRE_SENERE_ENN_PERIODENS_DATO_TOM_FEILAARSAK = "Periodens DatoFom kan ikke være senere enn periodens DatoTom";

	public void validate(HentVarselForBrukerRequest request) throws HentVarselForBrukerUgyldigInput {
		validateBruker(request.getBruker());
		validatePeriode(request.getPeriode());
	}

	private void validatePeriode(Periode periode) throws HentVarselForBrukerUgyldigInput {
		validateTomDatoMustBeAtLeastFomDato(periode);
		valdiateTomDatoCantBeInTheFuture(periode);
	}

	private void validateTomDatoMustBeAtLeastFomDato(Periode periode) throws HentVarselForBrukerUgyldigInput {
		if (periode.getFom() != null && periode.getTom() != null &&
				periode.getFom().toGregorianCalendar().after(periode.getTom().toGregorianCalendar())) {
			throwValidationException(UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_FEILMELDING,
					UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_FEILMELDING,
					PERIODENS_DATO_FOM_KAN_IKKE_VÆRE_SENERE_ENN_PERIODENS_DATO_TOM_FEILAARSAK,
					now());
		}
	}


	private void valdiateTomDatoCantBeInTheFuture(Periode periode) throws HentVarselForBrukerUgyldigInput {
		if (periode.getFom() != null && periode.getTom() != null &&
				periode.getTom().toGregorianCalendar().after(GregorianCalendar.getInstance())) {
			throwValidationException(UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_FEILMELDING,
					UGYLDIG_BRUK_AV_DATO_FOM_OG_DATO_TOM_FEILMELDING,
					PERIODENS_TOM_KAN_IKKE_VÆRE_SENERE_ENN_DAGENS_DATO_FEILAARSAK,
					now());
		}
	}

	private void validateBruker(Aktoer bruker) throws HentVarselForBrukerUgyldigInput {
		if(bruker == null) {
			throwValidationException(PAAKREVD_INPUTPARAMETER_ER_IKKE_SATT_FEILMELDING,
					PAAKREVD_INPUTPARAMETER_ER_IKKE_SATT_FEILMELDING,
					INPUTPARAMETER_BRUKER_MANGLER_FEILAARSAK,
					now());
		}
	}

	private void throwValidationException(String message, String feilmelding, String feilaarsak, XMLGregorianCalendar tidspunkt) throws HentVarselForBrukerUgyldigInput {
		UgydigInput faultInfo = new UgydigInput();
		faultInfo.setFeilmelding(feilmelding);
		faultInfo.setFeilaarsak(feilaarsak);
		faultInfo.setTidspunkt(tidspunkt);
		throw new HentVarselForBrukerUgyldigInput(message, faultInfo);
	}

	private XMLGregorianCalendar now() {
		return XmlGregorianConverter.toXmlGregorianCalendar(LocalDateTime.now());
	}
}
