package no.nav.varsel.wsconsumer.kodeverk;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkV2;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.EnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.SammensattKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkResponse;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Kodeverk V2 Ws Consumer
 *
 * @author lars Aune
 */
public class KodeverkConsumer {
	private static final int TIMEOUT_MINUTES = 10;
	private static final String SPRAAK_NORSK_BOKMAAL = "nb";
	public static final String PERSONIDENTER_KODEVERKSNAVN = "Personidenter";

	@Inject
	private KodeverkV2 kodeverkV2;

	private LoadingCache<String, List<Kode>> kodeverk = CacheBuilder.newBuilder()
			.expireAfterWrite(TIMEOUT_MINUTES, TimeUnit.MINUTES)
			.build(new CacheLoader<String, List<Kode>>() {
				@Override
				public List<Kode> load(String kodeverksnavn) throws Exception {
					return createKodeverk(kodeverksnavn);
				}
			});

	private List<Kode> createKodeverk(String kodeverksnavn) throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
		HentKodeverkResponse hentKodeverkResponse = kodeverkV2.getKodeverkV2().hentKodeverk(hentkodeverkRequest(kodeverksnavn));

		Kodeverk kodeverk =	hentKodeverkResponse.getKodeverk();

		List<Kode> result = new ArrayList<>();

		if (kodeverk instanceof EnkeltKodeverk) {
			result.addAll(((EnkeltKodeverk) kodeverk).getKode());
		} else if (kodeverk instanceof SammensattKodeverk) {
			((SammensattKodeverk) kodeverk).getBrukerKodeverk().
					forEach(enkeltKodeverk -> result.addAll((enkeltKodeverk).getKode()));
		}

		return result;
	}

	private HentKodeverkRequest hentkodeverkRequest(String kodeverksnavn) {
		HentKodeverkRequest request = new HentKodeverkRequest();
		request.setNavn(kodeverksnavn);
		request.setSpraak(SPRAAK_NORSK_BOKMAAL);
		return request;
	}

	public boolean hasPersonIdenterKode(String kodenavn) throws ExecutionException {
		return hasKode(PERSONIDENTER_KODEVERKSNAVN, kodenavn);
	}
	public boolean hasKode(String kodeverksnavn, String kodenavn) throws ExecutionException {
		Assert.notNull(kodeverksnavn, "Parameteren kodeverksnavn kan ikke være null.");
		Assert.notNull(kodenavn, "Parameteren kodenavn kan ikke være null.");
		LocalDateTime now = LocalDateTime.now();

		return kodeverk.get(kodeverksnavn).stream().
				filter(kode ->
						kode.getNavn().equals(kodenavn) &&	kodeIsValidAt(kode, now)).collect(Collectors.toList()).size() > 0;
	}

	private boolean kodeIsValidAt(Kode kode, LocalDateTime time) {

		return kode.getGyldighetsperiode().isEmpty() ||
				kode.getGyldighetsperiode().stream().anyMatch(periode ->
				(periode.getFom() == null && periode.getTom() == null) ||
						(periode.getFom() == null &&
								beforeOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getTom()))) ||
						(periode.getTom() == null &&
								afterOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getFom()))) ||
						((afterOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getFom())) &&
								(beforeOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getTom())))))
		);
	}

	private boolean afterOrEqual(LocalDateTime time, LocalDateTime other) {
		return time.isAfter(other) || time.isEqual(other);
	}

	private boolean beforeOrEqual(LocalDateTime time, LocalDateTime other) {
		return time.isBefore(other) || time.isEqual(other);
	}
}
