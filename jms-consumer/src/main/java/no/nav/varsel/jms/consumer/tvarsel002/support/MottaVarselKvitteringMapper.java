package no.nav.varsel.jms.consumer.tvarsel002.support;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringStatusTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;

/**
 * Mapper for {@link no.nav.varsel.jms.consumer.tvarsel002.VarselKvitteringConsumer}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class MottaVarselKvitteringMapper {


	/**
	 * Maps from {@link VarselKvittering} to {@link MottaVarselKvitteringTo}
	 *
	 * @param varselKvittering The object to map from
	 * @return The mapped object
	 */
	public MottaVarselKvitteringTo map(VarselKvittering varselKvittering) {
		MottaVarselKvitteringTo to = new MottaVarselKvitteringTo();
		to.setVarselId(varselKvittering.getVarselId());
		to.setMottakerInformasjon(varselKvittering.getMottakerinformasjon());
		to.setUtsendingstidspunkt(varselKvittering.getUtsendingstidspunkt() == null ? null :
				varselKvittering.getUtsendingstidspunkt().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
		to.setStatus(varselKvittering.getStatus() == null ? null :
				MottaVarselKvitteringStatusTo.valueOf(varselKvittering.getStatus().toUpperCase()));
		to.setFeilmelding(varselKvittering.getFeilmelding());
		return to;
	}
}
