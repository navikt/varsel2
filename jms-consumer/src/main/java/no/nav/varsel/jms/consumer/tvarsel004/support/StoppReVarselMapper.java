package no.nav.varsel.jms.consumer.tvarsel004.support;

import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.StoppReVarsel;
import no.nav.varsel.service.tvarsel004.to.StoppReVarselTo;

/**
 * Mapper for {@link StoppReVarsel}
 *
 * @author Hiep Luong Nguyen, Computas
 */
public class StoppReVarselMapper {
	public StoppReVarselTo map(StoppReVarsel stoppReVarsel) {
		StoppReVarselTo to = new StoppReVarselTo();
		to.setVarselbestillingId(stoppReVarsel.getVarselbestillingId());
		return to;
	}
}