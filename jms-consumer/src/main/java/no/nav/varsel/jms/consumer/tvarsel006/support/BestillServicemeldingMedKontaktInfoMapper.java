package no.nav.varsel.jms.consumer.tvarsel006.support;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Aktoer;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.AktoerId;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Kontaktinformasjon;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Parameter;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.Person;
import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ServicemeldingMedKontaktinformasjon;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import no.nav.varsel.service.to.AktoerBestillingTo;
import no.nav.varsel.service.to.BestillVarselTo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps {@link ServicemeldingMedKontaktinformasjon} to {@link BestillVarselTo}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class BestillServicemeldingMedKontaktInfoMapper {

	public BestillVarselTo map(ServicemeldingMedKontaktinformasjon from) {
		BestillVarselTo to = new BestillVarselTo();
		to.setOrgNr(from.getTilhoerendeOrganisasjon() != null ? from.getTilhoerendeOrganisasjon().getOrgnummer() : null);
		mapKontaktInformasjon(from, to);
		to.setVarseltypeId(from.getVarseltypeId());
		to.setParameters(map(from.getParameterListe()));
		map(from.getMottaker(), to);
		to.setUtloepstidspunkt(XmlGregorianConverter.toLocalDateTime(from.getUtloepstidspunkt()));
		return to;
	}

	private void mapKontaktInformasjon(ServicemeldingMedKontaktinformasjon input, BestillVarselTo to) {
		for (Kontaktinformasjon kontaktinformasjon : input.getKontaktinformasjonListe()) {
			String kanal = kontaktinformasjon.getKanal() != null ? kontaktinformasjon.getKanal().getValue() : null;
			if (KanalCode.SMS.getKommunikasjonskanal().equals(kanal)) {
				to.setMobiltelefonnummer(kontaktinformasjon.getKontaktinformasjon());
			} else if (KanalCode.EPOST.getKommunikasjonskanal().equals(kanal)) {
				to.setEpost(kontaktinformasjon.getKontaktinformasjon());
			} else {
				throw new IllegalArgumentException("Invalid kommunikajsonskanal=" + kanal);
			}
		}
	}

	private Map<String, String> map(List<Parameter> parameterListe) {
		HashMap<String, String> map = new HashMap<>();
		parameterListe.forEach(p -> map.put(p.getKey(), p.getValue()));
		return map;
	}

	private void map(Aktoer fromAktoer, AktoerBestillingTo to) {
		if (fromAktoer instanceof AktoerId) {
			to.setAktoerId(((AktoerId) fromAktoer).getAktoerId());
		} else if (fromAktoer instanceof Person) {
			to.setPersonIdent(((Person) fromAktoer).getIdent());
		} else {
			throw new IllegalArgumentException("Could not map invalid AktoerType=" + fromAktoer.getClass());
		}
	}
}
