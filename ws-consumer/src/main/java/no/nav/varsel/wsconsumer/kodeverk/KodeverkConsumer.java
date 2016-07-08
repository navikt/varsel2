package no.nav.varsel.wsconsumer.kodeverk;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.EnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Periode;
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
	private KodeverkPortType kodeverkPortType;

	private LoadingCache<String, List<Kode>> kodeverkCache = CacheBuilder.newBuilder()
			.expireAfterWrite(TIMEOUT_MINUTES, TimeUnit.MINUTES)
			.build(new CacheLoader<String, List<Kode>>() {
				@Override
				public List<Kode> load(String kodeverksnavn) throws Exception {
					return createKodeverk(kodeverksnavn);
				}
			});

	private List<Kode> createKodeverk(String kodeverksnavn) throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
		HentKodeverkResponse hentKodeverkResponse = kodeverkPortType.hentKodeverk(hentkodeverkRequest(kodeverksnavn));
		Kodeverk kodeverk = hentKodeverkResponse.getKodeverk();

		List<Kode> result = new ArrayList<>();

		if (kodeverk instanceof EnkeltKodeverk) {
			result.addAll(filterCurrentCodes(((EnkeltKodeverk) kodeverk).getKode()));
		} else if (kodeverk instanceof SammensattKodeverk) {
			((SammensattKodeverk) kodeverk).getBrukerKodeverk().
					forEach(enkeltKodeverk -> result.addAll(filterCurrentCodes((enkeltKodeverk).getKode())));
		}

		return result;
	}

	private List<Kode> filterCurrentCodes(List<Kode> koder) {
		return koder.stream().filter(kode -> kodeIsValidAt(kode, LocalDateTime.now())).collect(Collectors.toList());
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

		return kodeverkCache.get(kodeverksnavn).stream().anyMatch(kode -> kode.getNavn().equals(kodenavn));
	}

	private boolean kodeIsValidAt(Kode kode, LocalDateTime tidspunkt) {

		return kode.getGyldighetsperiode().isEmpty() ||
				kode.getGyldighetsperiode().stream().anyMatch(periode ->
						periodenManglerStartOgSlutt(periode) ||
								tidspunktFoerEllerLikSluttForPeriodeUtenStart(tidspunkt, periode) ||
								tidspunktEtterEllerLikStartForPeriodeUtenSlutt(tidspunkt, periode) ||
								tidspunktIPeriodeMedStartOgSlutt(tidspunkt, periode)
				);
	}

	private boolean tidspunktIPeriodeMedStartOgSlutt(LocalDateTime time, Periode periode) {
		return afterOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getFom())) &&
				(beforeOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getTom())));
	}

	private boolean tidspunktEtterEllerLikStartForPeriodeUtenSlutt(LocalDateTime time, Periode periode) {
		return periode.getTom() == null &&
				afterOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getFom()));
	}

	private boolean tidspunktFoerEllerLikSluttForPeriodeUtenStart(LocalDateTime time, Periode periode) {
		return periode.getFom() == null &&
				beforeOrEqual(time, XmlGregorianConverter.toLocalDateTime(periode.getTom()));
	}

	private boolean periodenManglerStartOgSlutt(Periode periode) {
		return periode.getFom() == null && periode.getTom() == null;
	}

	private boolean afterOrEqual(LocalDateTime time, LocalDateTime other) {
		return time.isAfter(other) || time.isEqual(other);
	}

	private boolean beforeOrEqual(LocalDateTime time, LocalDateTime other) {
		return time.isBefore(other) || time.isEqual(other);
	}
}
