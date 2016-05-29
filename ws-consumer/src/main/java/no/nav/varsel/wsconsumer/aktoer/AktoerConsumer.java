package no.nav.varsel.wsconsumer.aktoer;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;

/**
 * Aktoer Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerConsumer {

	public AktoerTo hentIdent(AktoerTo request) {
		AktoerTo response = new AktoerTo();
		MottakerType type = MottakerType.values()[(request.getMottakerType().ordinal() + 1) % 2];
		response.setMottakerType(type);
		response.setIdent(request.getIdent() + type.toString().substring(0, 1));
		return response;
	}
}
