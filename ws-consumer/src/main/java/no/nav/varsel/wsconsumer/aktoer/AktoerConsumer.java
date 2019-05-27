package no.nav.varsel.wsconsumer.aktoer;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdResponse;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.inject.Inject;

/**
 * Aktoer V2 Ws Consumer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerConsumer {

	@Inject
	private AktoerV2 aktoerV2;

	@Retryable(exclude = {HentIdentForAktoerIdPersonIkkeFunnet.class, HentAktoerIdForIdentPersonIkkeFunnet.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000L, multiplier = 2))
	public AktoerTo hentIdent(AktoerTo requestTo) throws HentIdentForAktoerIdPersonIkkeFunnet, HentAktoerIdForIdentPersonIkkeFunnet {
		AktoerTo responseTo = new AktoerTo();

		if (requestTo.getMottakerType() == MottakerType.AKTOER) {
			HentIdentForAktoerIdRequest request = new HentIdentForAktoerIdRequest();
			request.setAktoerId(requestTo.getIdent());
			HentIdentForAktoerIdResponse response = aktoerV2.hentIdentForAktoerId(request);
			responseTo.setMottakerType(MottakerType.PERSON);
			responseTo.setIdent(response.getIdent());
		} else {
			HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
			request.setIdent(requestTo.getIdent());
			HentAktoerIdForIdentResponse response = aktoerV2.hentAktoerIdForIdent(request);
			responseTo.setMottakerType(MottakerType.AKTOER);
			responseTo.setIdent(response.getAktoerId());
		}

		return responseTo;
	}
}
