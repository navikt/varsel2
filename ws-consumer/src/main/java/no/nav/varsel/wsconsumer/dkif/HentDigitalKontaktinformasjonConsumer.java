package no.nav.varsel.wsconsumer.dkif;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

/**
 * HentDigitalKontaktinformasjon Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class HentDigitalKontaktinformasjonConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(HentDigitalKontaktinformasjonConsumer.class);

	@Inject
	private DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1;
	@Inject
	private HentDigitalKontaktinformasjonMapper mapper;

	@Inject
	private VarselKanalDecider varselKanalDecider;

	public KontaktregisterTo hentDigitalKontaktinformasjonAndDecideKanal(String personIdent, Set<KanalCode> preferertKanal) {
		KontaktregisterTo kontaktregisterTo = hentDigitalKontaktinformasjon(personIdent);

		Collection<KanalCode> kanaler = varselKanalDecider.decideKanaler(kontaktregisterTo, preferertKanal);
		kontaktregisterTo.setKanaler(kanaler);
		return kontaktregisterTo;
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 2))
	public KontaktregisterTo hentDigitalKontaktinformasjon(String personIdent) {
		HentDigitalKontaktinformasjonRequest request = new HentDigitalKontaktinformasjonRequest();
		request.setPersonident(personIdent);
		HentDigitalKontaktinformasjonResponse response;
		try {
			response = digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(request);
		} catch (HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet |
				HentDigitalKontaktinformasjonPersonIkkeFunnet |
				HentDigitalKontaktinformasjonSikkerhetsbegrensing e) {
			LOG.warn(String.format("Feil mot DKIF %s: %s %s", e.getClass().getSimpleName(), e.getMessage(), personIdent));
			return new KontaktregisterTo();
		}
		KontaktregisterTo kontaktregisterTo = mapper.map(response);
		kontaktregisterTo.cleanExpiredInfo();
		return kontaktregisterTo;
	}
}
