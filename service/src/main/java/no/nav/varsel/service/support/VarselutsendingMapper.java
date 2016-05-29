package no.nav.varsel.service.support;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;

/**
 * Mapper for Varselutsending
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingMapper {

	public VarselutsendingTo map(Varselbestilling varselbestilling) {
		VarselutsendingTo to = new VarselutsendingTo();

		return to;

	}
}
