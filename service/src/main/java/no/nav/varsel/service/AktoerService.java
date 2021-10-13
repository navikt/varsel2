package no.nav.varsel.service;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.service.to.AktoerBestillingTo;
import no.nav.varsel.wsconsumer.pdl.PdlIdentConsumer;
import no.nav.varsel.wsconsumer.pdl.support.AktoerIkkeFunnetException;

import javax.inject.Inject;

import static no.nav.varsel.domain.to.AktoerTo.newAktoerId;
import static no.nav.varsel.domain.to.AktoerTo.newPersonIdent;
import static no.nav.varsel.domain.to.MottakerType.AKTOER;
import static no.nav.varsel.domain.to.MottakerType.PERSON;

/**
 * Tjenesten skal finne manglende ident for bruker.
 * Kommer bestilling med aktørId som input til tjenesten, finner den folkeregisterident.
 * Kommer bestilling med folkeregisterident som input til tjenesten, finner den aktørId.
 */
public class AktoerService {

	@Inject
	private PdlIdentConsumer pdlIdentConsumer;

	public AktoerTo findMissingAktoer(AktoerBestillingTo aktoerBestillingTo) {
		AktoerTo origAktoer = aktoerBestillingTo.createAktoerTo();

		if (origAktoer.getMottakerType() == AKTOER) {
			return newPersonIdent(pdlIdentConsumer.hentFolkeregisterIdent(origAktoer.getIdent()));
		} else if (origAktoer.getMottakerType() == PERSON) {
			return newAktoerId(pdlIdentConsumer.hentAktoerId(origAktoer.getIdent()));
		} else {
			throw new AktoerIkkeFunnetException("Kunne ikke hente manglende ident. Mangler mottakerType. mottakertype=" +
					origAktoer.getMottakerType());
		}
	}
}
