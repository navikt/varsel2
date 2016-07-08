package no.nav.varsel.mock;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.repo.TestdataUtil.PERSON_IDENTER_BOST_KODENAVN;
import static no.nav.varsel.repo.TestdataUtil.PERSON_IDENTER_DNR_KODENAVN;
import static no.nav.varsel.repo.TestdataUtil.PERSON_IDENTER_FDAT_KODENAVN;
import static no.nav.varsel.repo.TestdataUtil.PERSON_IDENTER_FNR_KODENAVN;
import static no.nav.varsel.repo.TestdataUtil.PERSON_IDENTER_SOME_FUTURE_KODENAVN;
import static no.nav.varsel.repo.TestdataUtil.PERSON_IDENTER_SOME_OLD_KODENAVN;
import static no.nav.varsel.repo.TestdataUtil.PERSON_IDENTER_SOME_VALID_KODENAVN;
import static no.nav.varsel.wsconsumer.kodeverk.KodeverkConsumer.PERSONIDENTER_KODEVERKSNAVN;

import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.EnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Periode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkResponse;

import javax.jws.WebService;
import java.time.LocalDateTime;

/**
 * @author Lars Aune
 */
@WebService(
		name = "KodeverkPortType",
		targetNamespace = "http://nav.no/tjeneste/virksomhet/kodeverk/v2",
		serviceName = "Kodeverk_v2",
		portName = "Kodeverk_v2"
)
public class KodeverkPortTypeMock implements KodeverkPortType {

	@Override
	public FinnKodeverkListeResponse finnKodeverkListe(FinnKodeverkListeRequest finnKodeverkListeRequest) {
		return null;
	}

	@Override
	public HentKodeverkResponse hentKodeverk(HentKodeverkRequest hentKodeverkRequest) throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
		LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);
		LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
		LocalDateTime fiveDaysIntoTheFuture = LocalDateTime.now().plusDays(5);

		HentKodeverkResponse result = new HentKodeverkResponse();
		EnkeltKodeverk kodeverk = new EnkeltKodeverk();
		kodeverk.setNavn(PERSONIDENTER_KODEVERKSNAVN);
		addKode(kodeverk, PERSON_IDENTER_FDAT_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_FNR_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_BOST_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_DNR_KODENAVN);
		addKode(kodeverk, PERSON_IDENTER_SOME_OLD_KODENAVN, tenDaysAgo, fiveDaysAgo);
		addKode(kodeverk, PERSON_IDENTER_SOME_FUTURE_KODENAVN, fiveDaysIntoTheFuture, null);
		addKode(kodeverk, PERSON_IDENTER_SOME_VALID_KODENAVN, fiveDaysAgo, fiveDaysIntoTheFuture);
		result.setKodeverk(kodeverk);
		return result;
	}

	@Override
	public void ping() {
	}

	private void addKode(EnkeltKodeverk kodeverk, String kodenavn) {
		Kode kode = new Kode();
		kode.setNavn(kodenavn);
		kodeverk.getKode().add(kode);
	}

	private void addKode(EnkeltKodeverk kodeverk, String kodenavn, LocalDateTime fom, LocalDateTime tom) {
		Kode kode = new Kode();
		kode.setNavn(kodenavn);
		Periode periode = new Periode();
		periode.setFom(fom != null ? toXmlGregorianCalendar(fom) : null);
		periode.setTom(tom != null ? toXmlGregorianCalendar(tom) : null);
		kode.getGyldighetsperiode().add(periode);
		kodeverk.getKode().add(kode);
	}
}
