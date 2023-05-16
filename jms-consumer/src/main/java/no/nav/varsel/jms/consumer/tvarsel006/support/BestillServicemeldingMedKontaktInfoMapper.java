package no.nav.varsel.jms.consumer.tvarsel006.support;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSAktoer;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSAktoerId;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSKontaktinformasjon;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSParameter;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSPerson;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.WSServicemeldingMedKontaktinformasjon;
import no.nav.varsel.service.to.AktoerBestillingTo;
import no.nav.varsel.service.to.BestillVarselTo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;

public class BestillServicemeldingMedKontaktInfoMapper {

	public BestillVarselTo map(WSServicemeldingMedKontaktinformasjon from) {
		BestillVarselTo to = new BestillVarselTo();
		to.setOrgNr(from.getTilhoerendeOrganisasjon() != null ? from.getTilhoerendeOrganisasjon().getOrgnummer() : null);
		mapKontaktInformasjon(from, to);
		to.setVarseltypeId(from.getVarseltypeId());
		to.setParameters(map(from.getParameterListe()));
		map(from.getMottaker(), to);
		to.setUtloepstidspunkt(toLocalDateTime(from.getUtloepstidspunkt()));

		return to;
	}

	private void mapKontaktInformasjon(WSServicemeldingMedKontaktinformasjon input, BestillVarselTo to) {
		for (WSKontaktinformasjon kontaktinformasjon : input.getKontaktinformasjonListe()) {
			String kanal = kontaktinformasjon.getKanal() != null ? kontaktinformasjon.getKanal().getValue() : null;

			if (SMS.name().equals(kanal)) {
				to.setMobiltelefonnummer(kontaktinformasjon.getKontaktinformasjon());
			} else if (EPOST.name().equals(kanal)) {
				to.setEpost(kontaktinformasjon.getKontaktinformasjon());
			} else {
				throw new IllegalArgumentException("Ugyldig kommunikasjonskanal=" + kanal);
			}
		}
	}

	private Map<String, String> map(List<WSParameter> parameterListe) {
		HashMap<String, String> map = new HashMap<>();
		parameterListe.forEach(p -> map.put(p.getKey(), p.getValue()));

		return map;
	}

	private void map(WSAktoer fromAktoer, AktoerBestillingTo to) {
		if (fromAktoer instanceof WSAktoerId) {
			to.setAktoerId(((WSAktoerId) fromAktoer).getAktoerId());
		} else if (fromAktoer instanceof WSPerson) {
			to.setPersonIdent(((WSPerson) fromAktoer).getIdent());
		} else {
			throw new IllegalArgumentException("Could not map invalid AktoerType=" + fromAktoer.getClass());
		}
	}
}
