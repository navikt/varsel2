package no.nav.varsel.service;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.service.to.AktoerBestillingTo;
import no.nav.varsel.wsconsumer.aktoer.AktoerConsumer;
import no.nav.varsel.wsconsumer.aktoer.support.AktoerIkkeFunnetException;

import javax.inject.Inject;

/**
 * Service for Aktoer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerService {

	@Inject
	private AktoerConsumer aktoerConsumer;

	/**
	 * Fetch missing aktoer component and return the original
	 *
	 * @param aktoerBestillingTo the bestilling
	 * @return the original aktoer used in lookup
	 */
	public AktoerTo completeAktoerPersonIdent(AktoerBestillingTo aktoerBestillingTo) {
		AktoerTo origAktoer = aktoerBestillingTo.createAktoerTo();
		AktoerTo fetchedAktoer;
		try {
			fetchedAktoer = aktoerConsumer.hentIdent(origAktoer);
		} catch (HentIdentForAktoerIdPersonIkkeFunnet | HentAktoerIdForIdentPersonIkkeFunnet e) {
			throw new AktoerIkkeFunnetException("Kunne ikke hente manglende ident for " + origAktoer, e);
		}
		aktoerBestillingTo.setMottaker(fetchedAktoer);
		return origAktoer;
	}
}
